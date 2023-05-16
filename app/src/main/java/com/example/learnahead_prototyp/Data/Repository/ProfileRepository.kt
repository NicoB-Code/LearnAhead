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
import com.google.firebase.storage.ktx.storage


class ProfileRepository (
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : IProfileRepository{

    val TAG: String = "ProfileRepository"
    private val storage = Firebase.storage


    override suspend fun uploadImage(imageUri: Uri, user: User, onResult: (UiState<Uri>) -> Unit) {

        Log.d(TAG, "Starting uploadImage function - URI: $imageUri")

        val filename = UUID.randomUUID().toString()


// Lösche das alte Bild, bevor das neue hochgeladen wird
        deletePreviousImage(user) { success ->
            if (success) {
                // Das alte Bild wurde erfolgreich gelöscht, lade das neue Bild hoch
                val storageRefImage = storage.reference.child("images/$filename.jpg")
                storageRefImage.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                            saveImageUrlToFirestore(downloadUri.toString(), user)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Failure while uploading Image ${exception.message}")
                    }
            } else {
                // Fehler beim Löschen des alten Bildes
                Log.d(TAG, "Failed to delete previous image.")
            }
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
    private fun deletePreviousImage(user: User, onComplete: (Boolean) -> Unit) {
        val oldImageUrl = user.profileImageUrl
        Log.d(TAG, "Deleting old Image -> $oldImageUrl")

        if (oldImageUrl != null && oldImageUrl.isNotEmpty()) {
            val storageRefOldImage = storage.getReferenceFromUrl(oldImageUrl)
            storageRefOldImage.delete()
                .addOnSuccessListener {
                    // Das Bild wurde erfolgreich gelöscht. Setze den profileImageUrl im Firebase-Dokument zurück.
                    val userDocumentRef = database.collection("users").document(user.id)
                    userDocumentRef.update("profileImageUrl", "")
                        .addOnSuccessListener {
                            onComplete(true) // Erfolgreich gelöscht und Dokument aktualisiert
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Failed to update user document: ${exception.message}")
                            onComplete(false) // Fehler beim Aktualisieren des Dokuments
                        }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to delete previous image: ${exception.message}")
                    onComplete(false) // Fehler beim Löschen
                }
        } else {
            onComplete(true) // Kein vorheriges Bild vorhanden
        }
    }


}