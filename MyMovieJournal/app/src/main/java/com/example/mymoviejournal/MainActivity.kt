package com.example.mymoviejournal

import android.os.Bundle
import android.view.View
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val currentUser = remember { firebaseAuth.currentUser }
                    if (currentUser != null) {
                        HomeScreen(Modifier.padding(innerPadding))
                    } else {
                        AuthScreen(
                            onAuthSuccess = {
                                recreate() // Reload activity to refresh user session
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(title = { Text("Home") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Welcome to My Movie Journal!")
        }
    }
}
