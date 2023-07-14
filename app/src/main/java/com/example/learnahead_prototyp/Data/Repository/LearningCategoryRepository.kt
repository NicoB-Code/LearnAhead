package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Eine Klasse, die das ILearnCategoryRepository-Interface implementiert und Methoden enthält, um Lernkategorien aus der Datenbank abzurufen.
 *
 * @param database Die Firebase Firestore-Datenbankinstanz, auf die zugegriffen werden soll.
 */
class LearningCategoryRepository(
    val database: FirebaseFirestore
) : ILearnCategoryRepository {

    /**
     * Ruft die Lernkategorien für einen bestimmten Benutzer aus der Datenbank ab.
     *
     * @param user Der Benutzer, dessen Lernkategorien abgerufen werden sollen.
     * @param result Eine Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Lernkategorien oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorien erfolgreich abgerufen wurden oder nicht.
     */
    override fun getLearningCategories(user: User?, result: (UiState<List<LearningCategory>>) -> Unit) {
        if (user == null) {
            result.invoke(UiState.Failure("Benutzer ist null"))
            return
        }
        // Dokumentreferenz des Benutzers abrufen
        val userDocument = database.collection(FireStoreCollection.USER).document(user.id)
        // Das Benutzerdokument abrufen, um die Sammlungsreferenz der Lernkategorien zu erhalten
        userDocument.get()
            .addOnSuccessListener { userDocument ->
                val learningCategories = userDocument.toObject(User::class.java)?.learningCategories?.toMutableList() ?: mutableListOf()
                // Das Benutzerobjekt mit den abgerufenen Lernkategorien aktualisieren
                result.invoke(UiState.Success(learningCategories))
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Fügt eine neue Lernkategorie zur Datenbank hinzu.
     *
     * @param learningCategory Die Lernkategorie, die hinzugefügt werden soll.
     * @param result Eine Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorie erfolgreich hinzugefügt wurde oder nicht.
     */
    override fun addLearningCategory(learningCategory: LearningCategory, result: (UiState<LearningCategory?>) -> Unit) {
        // Dokument in der LERNKATEGORIE-Sammlung der Datenbank erstellen
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document()
        // Die ID der Lernkategorie auf die ID des erstellten Dokuments in der Datenbank festlegen
        learningCategory.id = document.id
        // Die Lernkategorie als Dokument zur Datenbank hinzufügen
        document
            .set(learningCategory)
            .addOnSuccessListener {
                // Bei Erfolg eine Erfolgsmeldung an den Aufrufer zurückgeben
                result.invoke(UiState.Success(learningCategory))
            }
            .addOnFailureListener {
                // Bei Fehlern eine Fehlermeldung an den Aufrufer zurückgeben
                result.invoke(UiState.Failure(
                    "Fehler beim Hinzufügen der Lernkategorie: ${it.localizedMessage}"
                ))
            }
    }

    /**
     * Aktualisiert eine vorhandene Lernkategorie in der Datenbank.
     *
     * @param learningCategory Die Lernkategorie, die aktualisiert werden soll.
     * @param result Eine Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorie erfolgreich aktualisiert wurde oder nicht.
     */
    override fun updateLearningCategory(learningCategory: LearningCategory, result: (UiState<LearningCategory?>) -> Unit) {
        // Dokument der Lernkategorie in der Datenbank abrufen
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document(learningCategory.id)
        document
            // Die Lernkategorie in der Datenbank aktualisieren
            .set(learningCategory)
            .addOnSuccessListener {
                result.invoke(UiState.Success(learningCategory))
            }
            .addOnFailureListener {
                // Fehlermeldung an den Aufrufer zurückgeben
                result.invoke(UiState.Failure(
                    "Fehler beim Aktualisieren der Lernkategorie: ${it.localizedMessage}"
                ))
            }
    }

    /**
     * Löscht eine vorhandene Lernkategorie aus der Datenbank.
     *
     * @param learningCategory Die Lernkategorie, die gelöscht werden soll.
     * @param result Eine Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorie erfolgreich gelöscht wurde oder nicht.
     */
    override fun deleteLearningCategory(learningCategory: LearningCategory, result: (UiState<String>) -> Unit) {
        // Zugriff auf das Lernkategorie-Dokument in der Datenbank anhand der Lernkategorie-ID
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document(learningCategory.id)
        // Das Lernkategorie-Dokument aus der Datenbank löschen
        document
            .delete()
            .addOnSuccessListener {
                // Eine Erfolgsmeldung an den Aufrufer zurückgeben
                result.invoke(UiState.Success("Die Lernkategorie wurde erfolgreich gelöscht"))
            }
            .addOnFailureListener {
                // Eine Fehlermeldung an den Aufrufer zurückgeben
                result.invoke(UiState.Failure(
                    "Fehler beim Löschen der Lernkategorie: ${it.localizedMessage}"
                ))
            }
    }
}