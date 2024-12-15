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

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocationFlow: StateFlow<Location?> = _userLocation

    // Hardcoded fallback cinema locations
    private val hardcodedCinemas = listOf(
        Cinema("Nordisk Film Biografer Sankt Knuds Torv", 56.149627, 10.203158, "Sankt Knuds Torv 15, 8000 Aarhus"),
        Cinema("CinemaxX Bruuns Galleri", 56.157022, 10.204882, "M. P. Bruuns Gade 25, 8000 Aarhus"),
        Cinema("Nordisk Film Biografer Tordenskjoldsgade", 56.172207, 10.197408, "Tordenskjoldsgade 21, 8200 Aarhus N")
    )

    @SuppressLint("MissingPermission")
    fun updateUserLocation(retryCount: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            if (hasLocationPermission()) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                try {
                    val location = fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        null
                    ).await()

                    if (location != null) {
                        Log.d("MapViewModel", "Got user location: ${location.latitude}, ${location.longitude}")
                        _userLocation.value = Location(location.latitude, location.longitude)
                        fetchNearbyCinemas(location.latitude, location.longitude)
                    } else {
                        Log.e("MapViewModel", "User location is null.")
                        if (retryCount < 3) {
                            Log.d("MapViewModel", "Retrying location... attempt ${retryCount + 2}")
                            delay(2000L)
                            updateUserLocation(retryCount + 1)
                        } else {
                            Log.e("MapViewModel", "Max retry attempts reached. Could not get location.")
                            _cinemas.value = hardcodedCinemas
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MapViewModel", "Error fetching user's location: ${e.message}", e)
                    _cinemas.value = hardcodedCinemas
                }
            } else {
                Log.e("MapViewModel", "Location permission not granted.")
                _cinemas.value = hardcodedCinemas
            }
        }
    }

    private fun fetchNearbyCinemas(lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Check permissions at runtime
                val hasFineLocation = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val hasWifiState = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_WIFI_STATE
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasFineLocation || !hasWifiState) {
                    Log.e("MapViewModel", "Permissions not granted: ACCESS_FINE_LOCATION or ACCESS_WIFI_STATE")
                    _cinemas.value = hardcodedCinemas
                    return@launch
                }

                // Prepare fields and request
                val fields = listOf(
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS,
                    Place.Field.TYPES
                )
                val request = FindCurrentPlaceRequest.newInstance(fields)

                // Make the request
                val response: FindCurrentPlaceResponse = placesClient.findCurrentPlace(request).await()
                Log.d("MapViewModel", "Places response returned ${response.placeLikelihoods.size} places.")

                val filteredCinemas = response.placeLikelihoods.mapNotNull { placeLikelihood ->
                    val place = placeLikelihood.place
                    val latLng = place.latLng

                    if (latLng != null &&
                        place.types?.contains(Place.Type.MOVIE_THEATER) == true &&
                        (place.name?.contains("Nordisk", true) == true ||
                                place.name?.contains("CinemaxX", true) == true)
                    ) {
                        Cinema(
                            name = place.name,
                            lat = latLng.latitude,
                            lng = latLng.longitude,
                            address = place.address ?: "No address available"
                        )
                    } else null
                }

                if (filteredCinemas.isEmpty()) {
                    Log.w("MapViewModel", "No matching cinemas found. Using hardcoded list.")
                    _cinemas.value = hardcodedCinemas
                } else {
                    Log.d("MapViewModel", "Filtered cinemas: ${filteredCinemas.map { it.name }}")
                    _cinemas.value = filteredCinemas
                }

            } catch (e: SecurityException) {
                Log.e("MapViewModel", "Permission denied: ${e.message}", e)
                _cinemas.value = hardcodedCinemas
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching cinemas: ${e.message}", e)
                _cinemas.value = hardcodedCinemas
            }
        }
    }


    fun searchCinemasByAddress(address: String) {
        Log.d("MapViewModel", "Searching for cinemas near: $address")
        _cinemas.value = hardcodedCinemas
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
