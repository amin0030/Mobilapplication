package com.example.mymoviejournal

import android.os.Bundle
import android.view.View
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mymoviejournal.components.TopNavigationMenu
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

        // Set content
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
            }
        }
    }
}