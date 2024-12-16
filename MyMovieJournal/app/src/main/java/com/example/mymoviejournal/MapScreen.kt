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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay

@Composable
fun MapScreen(navController: NavHostController?, mapViewModel: MapViewModel) {
    val context = LocalContext.current

    // State for location permission
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
        if (isGranted) {
            mapViewModel.updateUserLocation()
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            locationPermissionGranted = true
            mapViewModel.updateUserLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val cinemas by mapViewModel.cinemas.collectAsState()
    val userLoc by mapViewModel.userLocationFlow.collectAsState()
    var userAddress by remember { mutableStateOf("") }
    var showManualEntry by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(5000)
        if (userLoc == null) showManualEntry = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Cinemas") },
                navigationIcon = {
                    // Always display the Back button
                    IconButton(onClick = {
                        navController?.navigate("home") ?: Log.d("MapScreen", "No navController")
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Home")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                userLoc == null && !showManualEntry -> {
                    Text("Fetching your location...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                userLoc == null && showManualEntry -> {
                    Text("Could not fetch your location. Please enter your location:")
                    TextField(
                        value = userAddress,
                        onValueChange = { userAddress = it },
                        label = { Text("Enter city or address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = { mapViewModel.searchCinemasByAddress(userAddress) }) {
                        Text("Search")
                    }
                }
                else -> {
                    userLoc?.let { loc ->
                        Text("Your Location: ${loc.lat}, ${loc.lng}", fontWeight = FontWeight.Bold)
                        LazyColumn {
                            items(cinemas) { cinema ->
                                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(cinema.name, fontWeight = FontWeight.Bold)
                                        Text(cinema.address)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        GoogleMapComposable(
                            modifier = Modifier.fillMaxSize(),
                            onMapReady = { googleMap ->
                                val userLatLng = LatLng(loc.lat, loc.lng)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                                googleMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
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
            mapView.getMapAsync { googleMap -> onMapReady(googleMap) }
            mapView
        }
    )
}
