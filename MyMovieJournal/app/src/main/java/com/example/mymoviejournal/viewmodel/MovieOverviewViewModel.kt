package com.example.mymoviejournal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoviejournal.api.MovieDetailsResponse
import com.example.mymoviejournal.api.TMDbService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RatedMovie(
    val title: String = "",
    val rating: Double = 0.0,
    val posterPath: String = "",
    val releaseDate: String = "",
    val runtime: String = ""
)

class MovieOverviewViewModel : ViewModel() {

    private val _ratedMovies = MutableStateFlow<List<RatedMovie>>(emptyList())
    val ratedMovies: StateFlow<List<RatedMovie>> = _ratedMovies

    private val db = FirebaseFirestore.getInstance()
    private val tmdbService = TMDbService.create()
    private val apiKey = "939b476a789f7fcabff071dc47ebd640" // Replace with your TMDB API key

    fun fetchRatedMovies() {
        viewModelScope.launch {
            try {
                db.collection("UserJournal")
                    .get()
                    .addOnSuccessListener { result ->
                        val movies = mutableListOf<RatedMovie>()

                        result.forEach { document ->
                            val title = document.getString("title") ?: ""
                            val rating = document.getDouble("rating") ?: 0.0
                            val posterPath = document.getString("poster_path") ?: ""

                            viewModelScope.launch {
                                val movieDetails = fetchMovieDetailsByTitle(title)
                                movies.add(
                                    RatedMovie(
                                        title = movieDetails?.title ?: title,
                                        rating = rating,
                                        posterPath = "https://image.tmdb.org/t/p/w500/${movieDetails?.poster_path ?: posterPath}",
                                        releaseDate = movieDetails?.release_date ?: "Unknown",
                                        runtime = movieDetails?.runtime?.toString() ?: "N/A"
                                    )
                                )
                                _ratedMovies.value = movies.toList()
                            }
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchMovieDetailsByTitle(movieTitle: String): MovieDetailsResponse? {
        return try {
            // Search for the movie by its title
            val searchResponse = tmdbService.searchMovies(apiKey, movieTitle)
            val movie = searchResponse.results.firstOrNull()
            movie?.let { tmdbService.getMovieDetails(it.id, apiKey) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
