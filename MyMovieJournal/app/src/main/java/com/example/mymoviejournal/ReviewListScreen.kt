package com.example.mymoviejournal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ReviewListScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var reviews by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        db.collection("UserJournal")
            .get()
            .addOnSuccessListener { querySnapshot ->
                reviews = querySnapshot.documents.mapNotNull { it.data }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error fetching reviews: ${e.message}"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                TopAppBar(
                    title = { Text("My Reviews") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                reviews.isEmpty() -> Text(
                    text = "No reviews available.",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(reviews) { review ->
                        ReviewCardWithButton(review, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCardWithButton(review: Map<String, Any>, navController: NavController) {
    val title = review["title"] as? String ?: "Unknown Title"
    val comment = review["comment"] as? String ?: "No review available"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.h6)
            Text(
                text = "Review: $comment",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = { navController.navigate("reviewScreen/${title}") },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add/Update Review")
            }
        }
    }
}
