package com.example.mymoviejournal

import com.google.firebase.auth.FirebaseAuth

fun authenticateUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null)
            } else {
                callback(false, task.exception?.message ?: "Login failed")
            }
        }
}

fun registerUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null)
            } else {
                callback(false, task.exception?.message ?: "Registration failed")
            }
        }
}
