package com.example.mymoviejournal

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ReviewScreen(movieTitle: String) {
    var comment by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Hent eksisterende kommentar fra Firestore
    LaunchedEffect(movieTitle) {
        val db = FirebaseFirestore.getInstance()
        db.collection("UserJournal")
            .whereEqualTo("title", movieTitle)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    comment = document.getString("comment") ?: ""
                } else {
                    errorMessage = "Movie not found in UserJournal"
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = "Error fetching movie: ${e.message}"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Review for $movieTitle") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    Text("Write your review:", style = MaterialTheme.typography.h6)
                    TextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Your Review") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Update logic her */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Review")
                    }
                }
            }
        }
    }
}
