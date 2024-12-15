package com.example.mymoviejournal
import android.os.Bundle
import android.view.View
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mymoviejournal.ui.theme.MyMovieJournalTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
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
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    //aDD hOME sCREEN
                    composable("home") {
                        HomeScreen(
                            onNavigateToAddMovie = {
                                navController.navigate("addMovie")
                            }
                        )
                    }
                    // Add Movie Screen
                    composable("addMovie") {
                        AddMovieScreen(
                            onNavigateToReviewScreen = { movieTitle ->
                                navController.navigate("reviewScreen/$movieTitle")
                            }
                        )
                    }
                    // Add Review Screen
                    composable("reviewScreen/{movieTitle}") { backStackEntry ->
                        val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: ""

                        ReviewScreen(
                            movieTitle = movieTitle,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                }
            }
        }
    }
}