package com.example.mymoviejournal

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun MapScreen(mapViewModel: MapViewModel) {
    val context = LocalContext.current

    // Handle location permission
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
        if (isGranted) {
            Log.d("MapScreen", "Permission granted. Updating user location...")
            mapViewModel.updateUserLocation()
        } else {
            Log.e("MapScreen", "Permission NOT granted.")
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            locationPermissionGranted = true
            Log.d("MapScreen", "Already have permission. Updating user location...")
            mapViewModel.updateUserLocation()
        } else {
            Log.d("MapScreen", "Requesting location permission...")
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val cinemas by mapViewModel.cinemas.collectAsState()

    GoogleMapComposable(
        modifier = Modifier.fillMaxSize(),
        onMapReady = { googleMap ->
            if (!locationPermissionGranted) {
                Log.e("MapScreen", "Location permission not granted when map is ready.")
                return@GoogleMapComposable
            }

            mapViewModel.userLocation?.let { userLocation ->
                val userLatLng = LatLng(userLocation.lat, userLocation.lng)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                googleMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("You are here")
                )
            } ?: Log.e("MapScreen", "User location is null, cannot show marker.")

            if (cinemas.isNotEmpty()) {
                Log.d("MapScreen", "Showing ${cinemas.size} cinemas on the map.")
                cinemas.forEach { cinema ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(cinema.lat, cinema.lng))
                            .title(cinema.name)
                            .snippet(cinema.address)
                    )
                }
            } else {
                Log.w("MapScreen", "No cinemas to display on the map.")
            }
        }
    )
}

@Composable
fun GoogleMapComposable(
    modifier: Modifier = Modifier,
    onMapReady: (GoogleMap) -> Unit
) {
    val context = LocalContext.current

    // Create MapView and initialize in the AndroidView factory
    androidx.compose.ui.viewinterop.AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val mapView = MapView(ctx)
            mapView.onCreate(null)
            mapView.onResume()
            mapView.getMapAsync { googleMap ->
                onMapReady(googleMap)
            }
            mapView
        }
    )
}
