package com.example.mymoviejournal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RatedMovie(
    val title: String = "",
    val rating: Double = 0.0,
    val posterPath: String = "",
)

class MovieOverviewViewModel : ViewModel() {

    private val _ratedMovies = MutableStateFlow<List<RatedMovie>>(emptyList())
    val ratedMovies: StateFlow<List<RatedMovie>> = _ratedMovies

    private val db = FirebaseFirestore.getInstance()

    fun fetchRatedMovies() {
        viewModelScope.launch {
            try {
                db.collection("UserJournal")
                    .get()
                    .addOnSuccessListener { result ->
                        val movies = result.mapNotNull { document ->
                            RatedMovie(
                                title = document.getString("title") ?: "",
                                rating = document.getDouble("rating") ?: 0.0,
                                posterPath = document.getString("poster_path") ?: ""
                            )
                        }
                        _ratedMovies.value = movies
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
