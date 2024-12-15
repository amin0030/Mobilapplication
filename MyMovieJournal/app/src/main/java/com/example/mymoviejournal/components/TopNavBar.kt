package com.example.mymoviejournal.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
/* import com.example.mymoviejournal.components.NavItem */

/*
@Composable
fun TopNavBar(navController: NavController, currentTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        NavItem.AddMovie,
        NavItem.Review,
        NavItem.Recommendation
    )

    Column {
        TopAppBar(
            title = { Text("My Movie Journal") },
            backgroundColor = MaterialTheme.colors.primary,
            elevation = 4.dp
        )
        TabRow(
            selectedTabIndex = currentTabIndex,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            items.forEachIndexed { index, item ->
                Tab(
                    selected = currentTabIndex == index,
                    onClick = {
                        onTabSelected(index)
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    text = { Text(text = item.title) }
                )
            }
        }
    }
}
*/
