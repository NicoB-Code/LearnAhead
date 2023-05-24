package com.example.learnahead_prototyp.Data.Repository

import android.net.Uri
import android.util.Log
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.UUID


class ProfileRepository(
    val database: FirebaseFirestore,
    private val storageReference: StorageReference
) : IProfileRepository {

    val TAG: String = "ProfileRepository"

    /**
     * Diese Funktion managed den Vorgang der Aktualisierung des Profilbilds
     * @param imageUri Uri zum Bild, welches hochgeladen werden soll
     * @param user Der User der von der Aktualisierung betroffen ist.
     * @param result Wird für die Auswertung, ob die Funktion erfolgreich ist, verwendet
     */
    override fun uploadImage(imageUri: Uri, user: User, result: (UiState<String>) -> Unit) {

        Log.d(TAG, "Starting uploadImage function - URI: $imageUri")

        if (user.profileImageUrl.isNotEmpty()) {
            // Lösche das alte Bild, bevor das neue hochgeladen wird
            deletePreviousImage(user)
        }

        val filename = UUID.randomUUID().toString()
        val storageRefImage = storageReference.child("images/$filename.jpg")
        storageRefImage.putFile(imageUri)
            .addOnSuccessListener {
                storageRefImage.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d(TAG, "Image $downloadUri will be downloaded to the user")
                    saveImageUrlToFirestore(downloadUri.toString(), user, result)
                }
            }
            .addOnFailureListener {
                // Bei Fehlern wird eine Fehlermeldung an den Aufrufer zurückgegeben
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }


    /**
     * Diese Funktion speichert die Addresse des Bilds in Firestore im Dokument des dementsprechenden Users
     * @param imageUrl String der in der DB gespeichert wird
     * @param user Der User der von der Aktualisierung betroffen ist.
     * @param result Gibt an ob der Image Upload erfolgreich war oder ob es ein Fehler gab
     */
    private fun saveImageUrlToFirestore(
        imageUrl: String,
        user: User,
        result: (UiState<String>) -> Unit
    ) {
        // Baue Verbindung zu DB auf und ändere URL zum Profilbild ab
        val userDocumentRef = database.collection("user").document(user.id)
        userDocumentRef.update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Image was Uploaded successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    /**
     * Diese Funktion löscht das vorherige Profilbild nach der Aktualisierung
     * @param user Der User der von der Aktualisierung betroffen ist.
     */
    private fun deletePreviousImage(user: User) {
        // Hole Referenz zum alten Bild und lösche es
        val storageReferenceUrl = storageReference.storage.getReferenceFromUrl(user.profileImageUrl)
        val imageStorageReference =
            storageReferenceUrl.storage.getReference(storageReferenceUrl.path)
        try {
            // Lösche altes Bild
            imageStorageReference.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully deleted image")
                    val userDocumentRef = database.collection("user").document(user.id)
                    // update Profil-Dokument des betroffenen Users
                    userDocumentRef.update("profileImageUrl", "")
                        .addOnSuccessListener {
                            // Erfolgreich gelöscht
                        }
                        .addOnFailureListener { exception ->
                            Log.d(
                                TAG,
                                "Failed to update user document: ${exception.message}"
                            )
                            // Fehler beim Updaten des User Dokuments
                        }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to delete previous image: ${exception.message}")
                    // Fehler beim Löschen des Bilds
                }

        } catch (e: Exception) {
            // Exception beim Löschen des Bilds
            Log.e(
                TAG,
                "Exception occurred while trying to delete the image: ${e.message}",
                e
            )
        }
    }
}



