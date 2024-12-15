package com.example.mymoviejournal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.*
import androidx.compose.runtime.Composable
<<<<<<< HEAD
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import com.example.mymoviejournal.components.TopNavigationMenu

@Composable
fun HomeScreen(
    navController: NavController
) {
=======
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(onLogout: () -> Unit) {
>>>>>>> 91e182fc996d05930e07ebbc4c9b3fa6a8ea51e9
    Scaffold(
        topBar = { TopNavigationMenu(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
<<<<<<< HEAD
                .padding(innerPadding)
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.movie_banner),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Foreground Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title Text
                Text(
                    text = "Welcome to My Movie Journal!",
                    style = MaterialTheme.typography.h4.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Subtitle Text
                Text(
                    text = "Keep track of all the movies you love.",
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.White,
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
=======
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to My Movie Journal!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Sign out from Firebase
                    FirebaseAuth.getInstance().signOut()
                    // Trigger the onLogout callback
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
>>>>>>> 91e182fc996d05930e07ebbc4c9b3fa6a8ea51e9
            }
        }
    }
}
