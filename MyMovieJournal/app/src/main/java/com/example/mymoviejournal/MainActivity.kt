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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mymoviejournal.ui.theme.MyMovieJournalTheme
import com.example.mymoviejournal.viewmodel.MovieOverviewViewModel
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }


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

    if (isLoggedIn) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") { HomeScreen(navController) }
            composable("journal") { JournalScreen(navController) }
            composable("reviews") { ReviewListScreen(navController) }
            composable("addMovie") { AddMovieScreen(navController) }
            composable("map") {
                navController.context.startActivity(
                    MapActivity.createIntent(navController.context)
                )
            }
            composable("reviewScreen/{movieTitle}") { backStackEntry ->
                val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: ""
                ReviewScreen(movieTitle, navController)
            }

            composable("dailyRecommendation") {
                DailyRecommendationScreen(navController)
            }

            composable("movieOverview") {
                val viewModel: MovieOverviewViewModel = viewModel()
                MovieOverviewScreen(viewModel = viewModel, navController = navController)
            }
        }
    } else {
        AuthScreen(
            onAuthSuccess = { isLoggedIn = true }
        )
    }
}