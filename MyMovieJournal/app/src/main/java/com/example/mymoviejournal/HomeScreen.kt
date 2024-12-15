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
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import com.example.mymoviejournal.components.TopNavigationMenu

@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold(
        topBar = { TopNavigationMenu(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
            }
        }
    }
}
