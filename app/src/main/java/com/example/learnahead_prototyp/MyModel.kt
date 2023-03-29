package com.example.learnahead_prototyp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyModel {
    var myData: String = "Hello, MVVM!"
    var dbConnection = Firebase.firestore

    fun checkUserCredentials(username: String, password: String, onComplete: (Boolean) -> Unit) {
        // Get a reference to the "users" collection in Firestore
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        // Query the "users" collection for a document with the specified username and password
        usersCollection
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // If a document was found with the specified username and password, return true
                if (!querySnapshot.isEmpty) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while querying the Firestore database
                Log.e("Firestore", "Error checking user credentials", exception)
                onComplete(false)
            }
    }

}