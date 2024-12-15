package com.example.mymoviejournal

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
<<<<<<< HEAD
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
=======
import androidx.compose.material.*
import androidx.compose.runtime.*
>>>>>>> 91e182fc996d05930e07ebbc4c9b3fa6a8ea51e9
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mymoviejournal.components.TopNavigationMenu
import com.example.mymoviejournal.ui.theme.MyMovieJournalTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Places SDK if not initialized
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        // Enable edge-to-edge layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
        }

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Set content
        setContent {
            MyMovieJournalTheme {
<<<<<<< HEAD
                MyMovieJournalApp()
=======
                var isLoggedIn by remember { mutableStateOf(firebaseAuth.currentUser != null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isLoggedIn) {
                        HomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLogout = {
                                firebaseAuth.signOut()
                                isLoggedIn = false
                            },
                            onNavigateToMap = {
                                startActivity(MapActivity.createIntent(this))
                            }
                        )
                    } else {
                        AuthScreen(
                            onAuthSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
>>>>>>> 91e182fc996d05930e07ebbc4c9b3fa6a8ea51e9
            }
        }
    }
}

@Composable
<<<<<<< HEAD
fun MyMovieJournalApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopNavigationMenu(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("journal") { JournalScreen(navController) }
            composable("reviews") { ReviewListScreen(navController) }
            composable("addMovie") { AddMovieScreen(navController) }
            composable("reviewScreen/{movieTitle}") { backStackEntry ->
                val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: ""
                ReviewScreen(movieTitle)
=======
fun HomeScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text("Welcome to My Movie Journal!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToMap) {
                Text("Find Nearby Cinemas")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) {
                Text("Log Out")
>>>>>>> 91e182fc996d05930e07ebbc4c9b3fa6a8ea51e9
            }
        }
    }
}