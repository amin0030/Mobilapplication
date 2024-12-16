package com.example.mymoviejournal

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val tabs = listOf("Home", "Journal", "Add Movie", "Map", "Reviews")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("My Movie Journal") }
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                when (title) {
                                    "Home" -> navController.navigate("home")
                                    "Journal" -> navController.navigate("journal")
                                    "Add Movie" -> navController.navigate("addMovie")
                                    "Map" -> {
                                        navController.context.startActivity(
                                            MapActivity.createIntent(navController.context)
                                        )
                                    }
                                    "Reviews" -> navController.navigate("reviews")
                                }
                            },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Welcome to My Movie Journal!")
            }
        }
    }
}
