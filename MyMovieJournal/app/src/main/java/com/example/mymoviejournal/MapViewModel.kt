package com.example.mymoviejournal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Cinema(val name: String, val lat: Double, val lng: Double, val address: String)
data class Location(val lat: Double, val lng: Double)

class MapViewModel(private val context: Context) : ViewModel() {
    private val placesClient: PlacesClient = com.google.android.libraries.places.api.Places.createClient(context)
    private val _cinemas = MutableStateFlow<List<Cinema>>(emptyList())
    val cinemas: StateFlow<List<Cinema>> = _cinemas

    var userLocation: Location? = null
        private set

    /**
     * Attempts to fetch the user's current location up to 3 times if it returns null.
     * If permissions are not granted, it logs an error and does not retry.
     */
    @SuppressLint("MissingPermission")
    fun updateUserLocation(retryCount: Int = 0) {
        if (!hasLocationPermission()) {
            Log.e("MapViewModel", "Location permission not granted. Cannot fetch location.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                Log.d("MapViewModel", "Attempting to fetch user location... Attempt #${retryCount + 1}")
                val location = fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    Log.d("MapViewModel", "Got user location: ${location.latitude}, ${location.longitude}")
                    userLocation = Location(location.latitude, location.longitude)
                    // Once we have the location, fetch nearby cinemas
                    fetchNearbyCinemas(location.latitude, location.longitude)
                } else {
                    Log.w("MapViewModel", "User location is null.")
                    if (retryCount < 3) {
                        Log.d("MapViewModel", "Retrying to get location... Attempt ${retryCount + 2} of 3")
                        delay(2000L) // wait 2 seconds before retrying
                        updateUserLocation(retryCount + 1)
                    } else {
                        Log.e("MapViewModel", "Max retry attempts reached (3). Could not get location.")
                    }
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching user's location: ${e.message}", e)
            }
        }
    }

    /**
     * Fetches nearby cinemas from the Places API using the current user location.
     * If no cinemas are found, logs a warning.
     */
    private fun fetchNearbyCinemas(lat: Double, lng: Double) {
        if (!hasLocationPermission()) {
            Log.e("MapViewModel", "No permission, cannot fetch cinemas.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fields = listOf(
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
                )
                val request = FindCurrentPlaceRequest.newInstance(fields)

                Log.d("MapViewModel", "Fetching nearby places...")
                val response: FindCurrentPlaceResponse = placesClient.findCurrentPlace(request).await()
                Log.d("MapViewModel", "Places response returned ${response.placeLikelihoods.size} places.")

                val nearbyCinemas = response.placeLikelihoods.mapNotNull { placeLikelihood: PlaceLikelihood ->
                    val place: Place = placeLikelihood.place
                    val latLng: LatLng? = place.latLng
                    latLng?.let { location: LatLng ->
                        Cinema(
                            name = place.name ?: "Unknown",
                            lat = location.latitude,
                            lng = location.longitude,
                            address = place.address ?: "No address available"
                        )
                    }
                }

                if (nearbyCinemas.isEmpty()) {
                    Log.w("MapViewModel", "No places returned or no places had LAT_LNG. Possibly no cinemas nearby.")
                } else {
                    Log.d("MapViewModel", "Found ${nearbyCinemas.size} cinemas (or places) to display.")
                }

                // Update the state flow with the fetched cinemas
                _cinemas.value = nearbyCinemas
            } catch (e: SecurityException) {
                Log.e("MapViewModel", "SecurityException: Location permission required. ${e.message}", e)
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching nearby cinemas: ${e.message}", e)
            }
        }
    }

    /**
     * Checks if the FINE_LOCATION permission is granted.
     */
    private fun hasLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED
    }
}
