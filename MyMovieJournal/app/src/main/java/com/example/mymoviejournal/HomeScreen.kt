package com.example.mymoviejournal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val tabs = listOf("Home", "Journal", "Add Movie", "Map", "Reviews", "Daily Recommendation", "Movie Overview")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("My Movie Journal") },
                    backgroundColor = Color(0xFF6A1B9A),
                    contentColor = Color.White
                )
                TabRow(selectedTabIndex = selectedTabIndex, backgroundColor = Color(0xFF9C27B0)) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                when (title) {
                                    "Home" -> navController.navigate("home")
                                    "Journal" -> navController.navigate("journal")
                                    "Add Movie" -> navController.navigate("addMovie")
                                    "Map" -> {
                                        navController.context.startActivity(
                                            MapActivity.createIntent(navController.context)
                                        )
                                    }
                                    "Reviews" -> navController.navigate("reviews")
                                    "Daily Recommendation" -> navController.navigate("dailyRecommendation")
                                    "Movie Overview" -> navController.navigate("movieOverview")
                                }
                            },
                            text = { Text(title, color = Color.White) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Baggrundsbillede
            Image(
                painter = painterResource(id = R.drawable.movie_banner), // Sørg for billedet er i res/drawable
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Indhold ovenpå billedet
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to My Movie Journal",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Explore the world of movies!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Use the Movie Journal App to read reviews, add movies to your journal, and discover your daily film recommendation.",
                    fontSize = 18.sp,
                    color = Color(0xFFFFD700),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

            }
        }
    }
}
