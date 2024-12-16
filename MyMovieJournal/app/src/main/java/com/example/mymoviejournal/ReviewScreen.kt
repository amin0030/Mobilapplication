package com.example.mymoviejournal

import androidx.compose.foundation.layout.*
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
fun ReviewScreen(movieTitle: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var comment by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaved by remember { mutableStateOf(false) }


    LaunchedEffect(movieTitle) {
        db.collection("UserJournal")
            .whereEqualTo("title", movieTitle)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    comment = document.getString("comment") ?: ""
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
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                TopAppBar(
                    title = { Text("Review for $movieTitle") },
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
                        onClick = {

                            db.collection("UserJournal")
                                .whereEqualTo("title", movieTitle)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val documentId = querySnapshot.documents[0].id
                                        db.collection("UserJournal")
                                            .document(documentId)
                                            .update("comment", comment)
                                            .addOnSuccessListener {
                                                isSaved = true
                                                navController.navigate("reviews")
                                            }
                                            .addOnFailureListener { e ->
                                                errorMessage = "Failed to save: ${e.message}"
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    errorMessage = "Failed to save: ${e.message}"
                                }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Review")
                    }


                    if (isSaved) {
                        LaunchedEffect(Unit) {
                            navController.navigate("reviews")
                        }
                    }
                }
            }
        }
    }
}
