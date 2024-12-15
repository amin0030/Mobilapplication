package com.example.mymoviejournal

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mymoviejournal.components.TopNavigationMenu
import com.example.mymoviejournal.ui.theme.MyMovieJournalTheme
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth

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

        setContent {
            MyMovieJournalTheme {
                MyMovieJournalApp()
            }
        }
    }
}

@Composable
fun MyMovieJournalApp() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

    Scaffold(
        topBar = { TopNavigationMenu(navController) }
    ) { innerPadding ->
        if (isLoggedIn) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            isLoggedIn = false
                        },
                        onNavigateToMap = {
                            // Start map activity
                            navController.context.startActivity(
                                MapActivity.createIntent(navController.context)
                            )
                        }
                    )
                }
                composable("journal") { JournalScreen(navController) }
                composable("reviews") { ReviewListScreen(navController) }
                composable("addMovie") { AddMovieScreen(navController) }
                composable("reviewList") { ReviewListScreen(navController) }
                composable("reviewScreen/{movieTitle}") { backStackEntry ->
                    val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: ""
                    ReviewScreen(movieTitle)
                }
            }
        } else {
            AuthScreen(
                onAuthSuccess = { isLoggedIn = true }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            }
        }
    }
}
