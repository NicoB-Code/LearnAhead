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
                val storageRefImage = storageReference.child("images/$filename.jpg")
                storageRefImage.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        storageReference.downloadUrl
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                            Log.d(TAG, "Image $downloadUri - will be downloaded to the user")
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

        Log.d(TAG, "User Obejct: $user")
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

        Log.d(TAG,"Trying to delete image $oldImageUrl")
        val imageStorageReference = storageReference.child(oldImageUrl)


        imageStorageReference.listAll()
            .addOnSuccessListener { listResult ->
                val imageExists = listResult.items.isNotEmpty()


                if (oldImageUrl != null && oldImageUrl.isNotEmpty()) {
                    if (imageExists) {
                        val storageRefOldImage =
                            storageReference.storage.getReferenceFromUrl(oldImageUrl)
                        storageRefOldImage.delete()
                            .addOnSuccessListener {
                                // Das Bild wurde erfolgreich gelöscht. Setze den profileImageUrl im Firebase-Dokument zurück.
                                val userDocumentRef =
                                    database.collection("users").document(user.id)
                                userDocumentRef.update("profileImageUrl", "")
                                    .addOnSuccessListener {
                                        onComplete(true) // Erfolgreich gelöscht und Dokument aktualisiert
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(
                                            TAG,
                                            "Failed to update user document: ${exception.message}"
                                        )
                                        onComplete(false) // Fehler beim Aktualisieren des Dokuments
                                    }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(
                                    TAG,
                                    "Failed to delete previous image: ${exception.message}"
                                )
                                onComplete(false) // Fehler beim Löschen
                            }
                    }
                } else {
                    onComplete(true) // Kein vorheriges Bild vorhanden
                }
            }
            .addOnFailureListener { exception ->
                // Wenn das ein String vorhanden ist, und das Objekt in Firebase nicht existiert, dann liegt ein Fehler vor und
                // der String für das Profilbild muss manuell zurückgesetzt werden
                val userDocumentRef = database.collection("users").document(user.id)
                userDocumentRef.update("profileImageUrl", "")
                Log.e(TAG, "Failed to check if object exists", exception)
            }

    }

    }


