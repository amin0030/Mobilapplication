package com.example.mymoviejournal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun JournalScreen(navController: NavHostController) {
    var movies by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Hent data fra Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("UserJournal")
            .get()
            .addOnSuccessListener { querySnapshot ->
                movies = querySnapshot.documents.mapNotNull { it.data }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error fetching movies: ${e.message}"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Journal") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                movies.isEmpty() -> Text(
                    text = "No movies added to your journal yet.",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(movies) { movie ->
                        JournalMovieCard(movie)
                    }
                }
            }
        }
    }
}

@Composable
fun JournalMovieCard(movie: Map<String, Any>) {
    val title = movie["title"] as? String ?: "Untitled" // Null-sikker titel
    val posterPath = movie["poster_path"] as? String
    val rating = (movie["rating"] as? Double)?.toString() ?: "N/A" // Null-sikker rating

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (posterPath != null) {
                AsyncImage(
                    model = posterPath,
                    contentDescription = "Poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Rating: $rating", style = MaterialTheme.typography.body1)
            }
        }
    }
}
