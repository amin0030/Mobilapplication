package com.example.mymoviejournal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.mymoviejournal.viewmodel.MovieOverviewViewModel
import com.example.mymoviejournal.viewmodel.RatedMovie

@Composable
fun MovieOverviewScreen(
    navController: NavController,
    viewModel: MovieOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val ratedMovies by viewModel.ratedMovies.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRatedMovies()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Overview") },
                backgroundColor = MaterialTheme.colors.primarySurface,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            if (ratedMovies.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(ratedMovies) { movie ->
                        MovieGridItem(movie, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieGridItem(movie: RatedMovie, navController: NavController) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(movie.posterPath),
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(movie.title, style = MaterialTheme.typography.subtitle1)
            Text("Rating: ${movie.rating}", color = Color.Gray)
            Text("Release Date: ${movie.releaseDate}", color = Color.Gray)
            Text("Runtime: ${movie.runtime}", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate("reviewScreen/${movie.title}") }) {
                Text("Edit Review")
            }
        }
    }
}
