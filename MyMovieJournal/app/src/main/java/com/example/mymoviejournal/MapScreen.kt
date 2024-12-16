package com.example.mymoviejournal

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay

@Composable
fun MapScreen(mapViewModel: MapViewModel) {
    val context = LocalContext.current

    // State for location permission
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

    // Check for permission on start
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
    val userLoc by mapViewModel.userLocationFlow.collectAsState()

    // Manual input for location
    var userAddress by remember { mutableStateOf("") }
    var showManualEntry by remember { mutableStateOf(false) }

    // If location is null after delay, show manual entry
    LaunchedEffect(Unit) {
        delay(5000)
        if (userLoc == null) {
            showManualEntry = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Cinemas") },
                backgroundColor = MaterialTheme.colors.primarySurface,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            when {
                userLoc == null && !showManualEntry -> {
                    Text("Fetching your location...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                userLoc == null && showManualEntry -> {
                    Text(
                        "Could not fetch your location. Please enter your location:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = userAddress,
                        onValueChange = { userAddress = it },
                        label = { Text("Enter city or address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            mapViewModel.searchCinemasByAddress(userAddress)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Search")
                    }
                }
                else -> {
                    userLoc?.let { loc ->
                        Text(
                            "Your Location: ${loc.lat}, ${loc.lng}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Nearby Cinemas:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Display cinemas in a list
                        LazyColumn(modifier = Modifier.fillMaxHeight(0.4f)) {
                            items(cinemas) { cinema ->
                                Card(
                                    elevation = 4.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(cinema.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text(cinema.address, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display Google Map
                        GoogleMapComposable(
                            modifier = Modifier.fillMaxSize(),
                            onMapReady = { googleMap ->
                                val userLatLng = LatLng(loc.lat, loc.lng)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                                googleMap.addMarker(
                                    MarkerOptions().position(userLatLng).title("You are here")
                                )

                                // Add cinema markers
                                cinemas.forEach { cinema ->
                                    googleMap.addMarker(
                                        MarkerOptions()
                                            .position(LatLng(cinema.lat, cinema.lng))
                                            .title(cinema.name)
                                            .snippet(cinema.address)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleMapComposable(
    modifier: Modifier = Modifier,
    onMapReady: (GoogleMap) -> Unit
) {
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
