package com.example.learnahead.Data.Repository

import android.net.Uri
import android.util.Log
import com.example.learnahead.Data.Model.User
import com.example.learnahead.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.UUID

/**
 * Eine Klasse, die das IProfileRepository-Interface implementiert und Methoden zur Verwaltung des Profilbild-Uploads enthält.
 *
 * @param database Die Firebase Firestore-Datenbankinstanz, auf die zugegriffen werden soll.
 * @param storageReference Die Storage-Referenz für den Zugriff auf Firebase Storage.
 */
class ProfileRepository(
    private val database: FirebaseFirestore,
    private val storageReference: StorageReference
) : IProfileRepository {

    private val TAG: String = "ProfileRepository"

    /**
     * Diese Funktion verwaltet den Vorgang des Profilbild-Uploads.
     *
     * @param imageUri Die Uri des hochzuladenden Bildes.
     * @param user Der Benutzer, dessen Profilbild aktualisiert werden soll.
     * @param result Eine Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     */
    override fun uploadImage(imageUri: Uri, user: User, result: (UiState<String>) -> Unit) {
        Log.d(TAG, "Starte uploadImage-Funktion - URI: $imageUri")

        if (user.profileImageUrl.isNotEmpty()) {
            // Lösche das vorherige Bild, bevor das neue hochgeladen wird
            deletePreviousImage(user)
        }

        val filename = UUID.randomUUID().toString()
        val storageRefImage = storageReference.child("images/$filename.jpg")
        storageRefImage.putFile(imageUri)
            .addOnSuccessListener {
                storageRefImage.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d(TAG, "Bild $downloadUri wird zum Benutzer heruntergeladen")
                    saveImageUrlToFirestore(downloadUri.toString(), user, result)
                }
            }
            .addOnFailureListener { exception ->
                // Bei Fehlern wird eine Fehlermeldung an den Aufrufer zurückgegeben
                result.invoke(UiState.Failure(
                    "Fehler beim Hochladen des Bildes: ${exception.localizedMessage}"
                ))
            }
    }

    /**
     * Diese Funktion speichert die Bild-URL in Firestore im Dokument des entsprechenden Benutzers.
     *
     * @param imageUrl Die URL des Bildes, die in der Datenbank gespeichert werden soll.
     * @param user Der Benutzer, dessen Profilbild aktualisiert werden soll.
     * @param result Eine Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     */
    private fun saveImageUrlToFirestore(imageUrl: String, user: User, result: (UiState<String>) -> Unit) {
        val userDocumentRef = database.collection("user").document(user.id)
        userDocumentRef.update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                result.invoke(UiState.Success("Bild wurde erfolgreich hochgeladen"))
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Failure(
                    "Fehler beim Speichern der Bild-URL: ${exception.localizedMessage}"
                ))
            }
    }

    /**
     * Diese Funktion löscht das vorherige Profilbild nach der Aktualisierung.
     *
     * @param user Der Benutzer, dessen Profilbild aktualisiert wurde.
     */
    private fun deletePreviousImage(user: User) {
        val storageReferenceUrl = storageReference.storage.getReferenceFromUrl(user.profileImageUrl)
        val imageStorageReference = storageReferenceUrl.storage.getReference(storageReferenceUrl.path)
        try {
            imageStorageReference.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Bild erfolgreich gelöscht")
                    val userDocumentRef = database.collection("user").document(user.id)
                    userDocumentRef.update("profileImageUrl", "")
                        .addOnSuccessListener {
                            // Erfolgreich aktualisiert
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Fehler beim Aktualisieren des Benutzerdokuments: ${exception.message}")
                            // Fehler beim Aktualisieren des Benutzerdokuments
                        }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Fehler beim Löschen des vorherigen Bildes: ${exception.message}")
                    // Fehler beim Löschen des Bildes
                }

        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Löschen des Bildes: ${e.message}", e)
        }
    }
}
