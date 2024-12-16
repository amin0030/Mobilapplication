package com.example.mymoviejournal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.mymoviejournal.viewmodel.RecommendationViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DailyRecommendationScreen() {
    val context = LocalContext.current
    val viewModel = remember { RecommendationViewModel(context) } // Pass context to ViewModel
    val movie by viewModel.movie.collectAsState()
    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Movie Recommendation") },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                movie == null -> {
                    CircularProgressIndicator() // Show spinner while loading
                }
                movie?.title == null -> {
                    Text(
                        text = "No recommendations available. Please try again later.",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.error
                    )
                }
                else -> {
                    val movieData = movie!!
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val imageUrl = "https://image.tmdb.org/t/p/w500/${movieData.poster_path}"
                        Image(
                            painter = rememberImagePainter(imageUrl),
                            contentDescription = "Movie Poster",
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Title: ${movieData.title}",
                            style = MaterialTheme.typography.h6
                        )
                        Text("Rating: ${movieData.vote_average}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Overview: ${movieData.overview}")

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val data = hashMapOf(
                                    "title" to movieData.title,
                                    "rating" to movieData.vote_average,
                                    "poster_path" to imageUrl,
                                    "timestamp" to System.currentTimeMillis()
                                )
                                db.collection("UserJournal")
                                    .add(data)
                                    .addOnSuccessListener {
                                        println("Movie successfully added to journal.")
                                    }
                                    .addOnFailureListener { e ->
                                        println("Error adding movie: $e")
                                    }
                            }
                        ) {
                            Text("Add to Journal")
                        }
                    }
                }
            }
        }
    }
}
