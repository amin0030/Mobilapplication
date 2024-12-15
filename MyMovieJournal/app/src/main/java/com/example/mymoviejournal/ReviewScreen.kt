package com.example.mymoviejournal

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ReviewScreen(
    movieTitle: String,
    onNavigateBack: () -> Unit
) {
    var comment by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Comment for $movieTitle") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Write your comment for the movie:",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Your Comment") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("UserJournal")
                        .whereEqualTo("title", movieTitle)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val documentId = querySnapshot.documents[0].id
                                db.collection("UserJournal").document(documentId)
                                    .update("comment", comment)
                                    .addOnSuccessListener {
                                        println("Comment added successfully for $movieTitle")
                                        onNavigateBack() // Navigate back on success
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Error adding comment: ${e.message}"
                                    }
                            } else {
                                errorMessage = "Movie not found in UserJournal"
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Error fetching movie: ${e.message}"
                        }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Comment")
            }
        }
    }
}
