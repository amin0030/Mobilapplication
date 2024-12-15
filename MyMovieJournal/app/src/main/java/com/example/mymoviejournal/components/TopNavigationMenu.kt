package com.example.mymoviejournal.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TopNavigationMenu(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("My Movie Journal") },
        actions = {
            // Menu-ikon
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }

            // DropdownMenu til navigation
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Home
                DropdownMenuItem(onClick = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                    expanded = false
                }) {
                    Icon(BottomNavItem.Home.icon, contentDescription = "Home")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(BottomNavItem.Home.title)
                }

                // Journal
                DropdownMenuItem(onClick = {
                    navController.navigate(BottomNavItem.Journal.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                    expanded = false
                }) {
                    Icon(BottomNavItem.Journal.icon, contentDescription = "Journal")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(BottomNavItem.Journal.title)
                }

                // Reviews
                DropdownMenuItem(onClick = {
                    navController.navigate(BottomNavItem.ReviewList.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                    expanded = false
                }) {
                    Icon(BottomNavItem.ReviewList.icon, contentDescription = "Reviews")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(BottomNavItem.ReviewList.title)
                }
            }
        }
    )
}
