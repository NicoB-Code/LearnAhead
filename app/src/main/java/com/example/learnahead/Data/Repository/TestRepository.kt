package com.example.learnahead.Data.Repository

import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.Test
import com.example.learnahead.Data.Model.User
import com.example.learnahead.Util.FireStoreCollection
import com.example.learnahead.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Eine Klasse, die das ITestRepository-Interface implementiert und Methoden enthält, um Tests aus der Datenbank abzurufen, hinzuzufügen, zu aktualisieren und zu löschen.
 *
 * @param database Die Firebase Firestore-Datenbankinstanz, auf die zugegriffen werden soll.
 */
class TestRepository(
    private val database: FirebaseFirestore
) : ITestRepository {

    /**
     * Funktion zum Abrufen der Tests für eine bestimmte Lernkategorie und einen Benutzer aus der Datenbank.
     *
     * @param user Der Benutzer, für den die Tests abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, für die die Tests abgerufen werden sollen.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Liste von Tests oder eine Fehlermeldung enthält.
     */
    override fun getTests(
        user: User?,
        learningCategory: LearningCategory?,
        result: (UiState<List<Test>>) -> Unit
    ) {
        // Überprüfen, ob learningCategory null ist
        if (learningCategory == null) {
            result(UiState.Failure("learningCategory ist null"))
            return
        }

        // Überprüfen, ob user null ist
        if (user == null) {
            result(UiState.Failure("user ist null"))
            return
        }

        // Dokumentreferenz des Benutzers abrufen
        val userDocumentRef = database.collection(FireStoreCollection.USER).document(user.id)

        // Benutzerdokument abrufen
        userDocumentRef.get()
            .addOnSuccessListener { userDocument ->
                val userObject = userDocument.toObject(User::class.java)
                val learningCategories = userObject?.learningCategories

                if (learningCategories.isNullOrEmpty()) {
                    result(UiState.Failure("Lernkategorien sind null oder leer"))
                    return@addOnSuccessListener
                }

                val targetLearningCategory =
                    learningCategories.find { it.id == learningCategory.id }

                if (targetLearningCategory == null) {
                    result(UiState.Failure("Lernkategorie nicht gefunden"))
                    return@addOnSuccessListener
                }

                result(UiState.Success(targetLearningCategory.tests))
            }
            .addOnFailureListener { exception ->
                result(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Funktion zum Hinzufügen eines Tests in die Datenbank.
     *
     * @param test Der Test, der hinzugefügt werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Erfolgsmeldung oder eine Fehlermeldung enthält.
     */
    override fun addTest(test: Test, result: (UiState<Test?>) -> Unit) {
        // Neues Dokument in der TEST-Sammlung der Datenbank erstellen
        val document = database.collection(FireStoreCollection.TEST).document()

        // ID des Tests als ID des erstellten Dokuments in der Datenbank festlegen
        test.id = document.id

        // Test als Dokument zur Datenbank hinzufügen
        document.set(test)
            .addOnSuccessListener {
                // Bei Erfolg wird eine Erfolgsmeldung an den Aufrufer zurückgegeben
                result(UiState.Success(test))
            }
            .addOnFailureListener { exception ->
                // Bei Fehlern wird eine Fehlermeldung an den Aufrufer zurückgegeben
                result(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Funktion zum Aktualisieren eines Tests in der Datenbank.
     *
     * @param test Der Test, der aktualisiert werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Meldung, ob der Test erfolgreich aktualisiert wurde oder nicht, enthält.
     */
    override fun updateTest(test: Test, result: (UiState<Test?>) -> Unit) {
        // Dokument des Tests in der Datenbank abrufen
        val document = database.collection(FireStoreCollection.TEST).document(test.id)

        // Test in der Datenbank aktualisieren
        document.set(test)
            .addOnSuccessListener {
                result(UiState.Success(test))
            }
            .addOnFailureListener { exception ->
                // Fehlermeldung an den Aufrufer zurückgeben
                result(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Funktion zum Löschen eines Tests aus der Datenbank.
     *
     * @param test Der Test, der gelöscht werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Erfolgsmeldung oder eine Fehlermeldung enthält.
     */
    override fun deleteTest(test: Test, result: (UiState<String>) -> Unit) {
        // Zugriff auf das Test-Dokument in der Datenbank basierend auf der Test-ID
        val document = database.collection(FireStoreCollection.TEST).document(test.id)

        // Test-Dokument aus der Datenbank löschen
        document.delete()
            .addOnSuccessListener {
                // Erfolgsmeldung an den Aufrufer zurückgeben
                result(UiState.Success("Test wurde erfolgreich gelöscht"))
            }
            .addOnFailureListener { exception ->
                // Fehlermeldung an den Aufrufer zurückgeben
                result(UiState.Failure(exception.localizedMessage))
            }
    }
}
