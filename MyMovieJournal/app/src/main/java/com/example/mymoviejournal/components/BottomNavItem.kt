package com.example.mymoviejournal.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    object Journal : BottomNavItem("Journal", Icons.Default.Favorite, "journal")
    object ReviewList : BottomNavItem("Reviews", Icons.Default.List, "reviewList")
}