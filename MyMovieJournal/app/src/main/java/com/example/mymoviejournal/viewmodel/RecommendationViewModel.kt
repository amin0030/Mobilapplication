package com.example.mymoviejournal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoviejournal.api.Movie
import com.example.mymoviejournal.api.TMDbService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecommendationViewModel : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    private val tmdbService = TMDbService.create()

    fun fetchDailyRecommendation(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = tmdbService.getTopRatedMovies(apiKey)
                if (response.results.isNotEmpty()) {
                    _movie.value = response.results.random()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
