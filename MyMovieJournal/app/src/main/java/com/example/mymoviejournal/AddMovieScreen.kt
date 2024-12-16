package com.example.mymoviejournal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.mymoviejournal.api.Movie
import com.example.mymoviejournal.api.TMDbService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun AddMovieScreen(
    navController: NavHostController,
    tmdbService: TMDbService = TMDbService.create()
) {
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    var isEndOfList by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Function to load movies
    fun loadMovies(page: Int) {
        scope.launch {
            isLoading = true
            try {
                val response = tmdbService.getPopularMovies(
                    apiKey = "939b476a789f7fcabff071dc47ebd640",
                    page = page
                )
                if (response.results.isNotEmpty()) {
                    movies = movies + response.results
                } else {
                    isEndOfList = true
                }
            } catch (e: Exception) {
                println("Error fetching movies: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        loadMovies(currentPage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a Movie") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(movies) { movie ->
                    MovieCard(movie = movie, onAddMovie = { movie, rating ->
                        addMovieToJournal(db, movie, rating)
                        navController.navigate("reviewScreen/${movie.title}")
                    })
                }

                // Show a loading indicator at the bottom when fetching more movies
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Detect when the user scrolls to the bottom of the list
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { lastVisibleItemIndex ->
                        if (!isLoading && !isEndOfList && lastVisibleItemIndex == movies.size - 1) {
                            currentPage += 1
                            loadMovies(currentPage)
                        }
                    }
            }
        }
    }
}

// Function to save the movie to Firestore
fun addMovieToJournal(db: FirebaseFirestore, movie: Movie, rating: Float) {
    val movieData = hashMapOf(
        "title" to movie.title,
        "poster_path" to "https://image.tmdb.org/t/p/w500/${movie.poster_path}",
        "rating" to rating,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("UserJournal")
        .add(movieData)
        .addOnSuccessListener {
            println("Successfully added movie: ${movie.title}")
        }
        .addOnFailureListener { e ->
            println("Error adding movie: ${e.message}")
        }
}

@Composable
fun MovieCard(movie: Movie, onAddMovie: (Movie, Float) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(5f) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Rate the Movie") },
            text = {
                Column {
                    Text("Choose a rating:")
                    Slider(
                        value = rating,
                        onValueChange = { rating = it },
                        valueRange = 1f..10f,
                        steps = 9
                    )
                    Text("Rating: ${rating.toInt()}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddMovie(movie, rating)
                        showDialog = false
                    }
                ) {
                    Text("Add to Journal")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500/${movie.poster_path}",
                contentDescription = "${movie.title} Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = movie.title, style = MaterialTheme.typography.h6)
                Button(onClick = { showDialog = true }) {
                    Text("Add to Journal")
                }
            }
        }
    }
}
