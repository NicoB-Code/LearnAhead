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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.suspendCancellableCoroutine


class ProfileRepository (
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : IProfileRepository {

    val TAG: String = "ProfileRepository"
    private val storage = Firebase.storage


    override suspend fun uploadImage(imageUri: Uri, user: User, onResult: (UiState<Uri>) -> Unit) {

        Log.d(TAG, "Starting uploadImage function - URI: $imageUri")

        val filename = UUID.randomUUID().toString()

        // Lösche das alte Bild, bevor das neue hochgeladen wird
        val success = deletePreviousImage(user)
        if (success) {
            // Das alte Bild wurde erfolgreich gelöscht, lade das neue Bild hoch
            val storageRefImage = storageReference.child("images/$filename.jpg")
            storageRefImage.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    storageRefImage.downloadUrl.addOnSuccessListener { downloadUri ->
                        Log.d(TAG, "Image $downloadUri will be downloaded to the user")
                        saveImageUrlToFirestore(downloadUri.toString(), user)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failure while uploading Image ${exception.message}")
                }
        } else {
            // Fehler beim Löschen des alten Bildes
            Log.d(TAG, "deletePreviousImage did not succeed")
        }
    }


    private fun saveImageUrlToFirestore(imageUrl: String, user: User) {

        Log.d(TAG, "User Obejct: $user")
        val userDocumentRef = database.collection("user").document(user.id)
        userDocumentRef.update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                Log.d(TAG, "Image URL added to DB")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Image URL could not be added to DB")
            }
    }

    private suspend fun deletePreviousImage(user: User): Boolean =
        suspendCancellableCoroutine { continuation ->
            val oldImageUrl = user.profileImageUrl
            var isResumed = false

            Log.d(TAG, "Trying to delete image $oldImageUrl")
            if (oldImageUrl == null || oldImageUrl.isEmpty()) {
                isResumed = true
                continuation.resume(true) {}
            } else {
                val storageReferenceUrl = storageReference.storage.getReferenceFromUrl(oldImageUrl)
                Log.d(TAG, "Path-$storageReferenceUrl.path")
                val imageStorageReference = storageReferenceUrl.storage.getReference(storageReferenceUrl.path)
                try {
                    imageStorageReference.delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "Successfully deleted image")
                            val userDocumentRef = database.collection("user").document(user.id)
                            userDocumentRef.update("profileImageUrl", "")
                                .addOnSuccessListener {
                                    if (!isResumed) {
                                        isResumed = true
                                        continuation.resume(true) {}
                                    } // Successfully deleted and updated document
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(
                                        TAG,
                                        "Failed to update user document: ${exception.message}"
                                    )
                                    if (!isResumed) {
                                        isResumed = true
                                        continuation.resume(false) {}
                                    } // Error updating document
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Failed to delete previous image: ${exception.message}")
                            if (!isResumed) {
                                isResumed = true
                                continuation.resume(false) {}
                            } // Error deleting image
                        }
                }catch (e: Exception) {
                    // Exception occurred while checking if image exists
                    Log.e(TAG, "Exception occurred while checking if image exists: ${e.message}", e)
                }
            }
        }
    }



