package com.example.mymoviejournal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.mymoviejournal.ui.theme.MyMovieJournalTheme

class MapActivity : ComponentActivity() {
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = MapViewModelFactory(applicationContext)
        mapViewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]

        setContent {
            MyMovieJournalTheme {
                MapScreen(mapViewModel)
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MapActivity::class.java)
        }
    }
}
