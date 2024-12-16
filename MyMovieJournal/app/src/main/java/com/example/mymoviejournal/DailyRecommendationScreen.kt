package com.example.mymoviejournal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.mymoviejournal.viewmodel.RecommendationViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DailyRecommendationScreen(viewModel: RecommendationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val movie by viewModel.movie.collectAsState()
    val db = FirebaseFirestore.getInstance()

    // Hent TMDb API Key fra BuildConfig
    val apiKey = BuildConfig.TMDB_API_KEY

    // Hent anbefalingen
    LaunchedEffect(Unit) {
        viewModel.fetchDailyRecommendation(apiKey)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daily Movie Recommendation") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            movie?.let { movie ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val imageUrl = "https://image.tmdb.org/t/p/w500/${movie.poster_path}"
                    Image(
                        painter = rememberImagePainter(imageUrl),
                        contentDescription = "Movie Poster",
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Title: ${movie.title}", style = MaterialTheme.typography.h6)
                    Text("Rating: ${movie.vote_average}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Overview: ${movie.overview}")

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val movieData = hashMapOf(
                            "title" to movie.title,
                            "rating" to movie.vote_average,
                            "poster_path" to imageUrl,
                            "timestamp" to System.currentTimeMillis()
                        )
                        db.collection("UserJournal")
                            .add(movieData)
                    }) {
                        Text("Add to Journal")
                    }
                }
            } ?: CircularProgressIndicator()
        }
    }
}
