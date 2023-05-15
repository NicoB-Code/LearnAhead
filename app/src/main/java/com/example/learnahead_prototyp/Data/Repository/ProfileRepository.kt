package com.example.learnahead_prototyp.Data.Repository

import android.net.Uri
import android.util.Log
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.FireStoreDocumentField
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import com.example.learnahead_prototyp.Util.toast




class ProfileRepository (
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : IProfileRepository{

    val TAG: String = "ProfileRepository"

    override suspend fun uploadImage(imageUri: Uri, user: User, onResult: (UiState<Uri>) -> Unit) {

        Log.d(TAG, "Starting uploadImage function - URI: $imageUri")

        val filename = UUID.randomUUID().toString()


        val storageRefImage = storageReference.child("images/$filename.jpg")
        storageRefImage.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveImageUrlToFirestore(downloadUri.toString(), user)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Failure while uploading Image ${exception.message}")
            }
    }

    private fun saveImageUrlToFirestore(imageUrl:String, user: User) {


        val userDocumentRef = database.collection("user").document(user.id)
        userDocumentRef.update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                Log.d(TAG,"Image URL added to DB")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG,"Image URL could not be added to DB")
            }
    }
}