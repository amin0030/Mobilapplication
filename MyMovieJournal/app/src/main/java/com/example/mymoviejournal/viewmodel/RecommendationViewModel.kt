package com.example.mymoviejournal.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoviejournal.R
import com.example.mymoviejournal.api.Movie
import com.example.mymoviejournal.api.TMDbService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecommendationViewModel(context: Context) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    private val tmdbService = TMDbService.create()


    private val apiKey: String = context.getString(R.string.tmdb_api_key)

    init {
        fetchDailyRecommendation()
    }

    private fun fetchDailyRecommendation() {
        viewModelScope.launch {
            try {
                Log.d("RecommendationViewModel", "Fetching daily recommendation...")
                val response = tmdbService.getTopRatedMovies(apiKey)
                if (response.results.isNotEmpty()) {
                    _movie.value = response.results.random()
                    Log.d("RecommendationViewModel", "Movie fetched: ${_movie.value?.title}")
                } else {
                    Log.e("RecommendationViewModel", "No results found in API response.")
                }
            } catch (e: Exception) {
                Log.e("RecommendationViewModel", "Error fetching recommendation: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
