package com.example.mymoviejournal.api

import android.media.Rating
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// TMDb API Service
interface TMDbService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(): TMDbService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TMDbService::class.java)
        }
    }
}

// Data classes for API Response
data class MovieResponse(
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val vote_average: Double

)


