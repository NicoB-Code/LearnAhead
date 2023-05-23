package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Klasse, die die Schnittstelle IGoalRepository implementiert und Methoden enthält, um Ziele aus der Datenbank zu holen.
 * @param database Die Firebase Firestore-Datenbank-Instanz, auf die zugegriffen werden soll.
 */
class LearningCategoryRepository(
    val database: FirebaseFirestore
) : ILearnCategoryRepository {

    /**
     * Funktion, um Ziele aus der Datenbank zu holen.
     * @param user Der Benutzer, dessen Lernkategorie abgerufen werden sollen.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Lernkategorien oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob es erfolgreich war oder nicht.
     */
    override fun getLearningCategories(user: User?, result: (UiState<List<LearningCategory>>) -> Unit) {
        if (user == null) {
            result.invoke(UiState.Failure("User is null"))
            return
        }
        // Get the user document reference
        val userDocument = database.collection(FireStoreCollection.USER).document(user.id)
        // Fetch the user document to get the learningCategories collection reference
        userDocument.get()
            .addOnSuccessListener { userDocument ->
                val learningCategories = userDocument.toObject(User::class.java)?.learningCategories?.toMutableList() ?: mutableListOf()
                // Update the user object with the retrieved goals
                result.invoke(UiState.Success(learningCategories))
            }

            .addOnFailureListener { exception ->
                result.invoke(UiState.Failure(exception.localizedMessage))
            }
    }


    /**
     * Funktion, um ein Ziel in der Datenbank zu erstellen.
     * @param learningCategory Die Lernkategorie, die erstellt werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorie erfolgreich erstellt wurde oder nicht.
     */
    override fun addLearningCategory(learningCategory: LearningCategory, result: (UiState<LearningCategory?>) -> Unit) {
        // Erstellung eines neuen Dokuments in der GOAL-Sammlung der Datenbank
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document()
        // Festlegen der ID des Ziels als ID des erstellten Dokuments in der Datenbank
        learningCategory.id = document.id
        // Hinzufügen des Ziels als Dokument zur Datenbank
        document
            .set(learningCategory)
            .addOnSuccessListener {
                // Bei Erfolg wird eine Erfolgsmeldung an den Aufrufer zurückgegeben
                result.invoke(
                    UiState.Success(learningCategory)
                )
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
     * Funktion zum Aktualisieren einer Lernkategorie in der Datenbank.
     * @param learningCategory Die Lernkategorie, das aktualisiert werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Meldung, ob die Lernkategorie erfolgreich aktualisiert wurde oder nicht.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Aktualisierung erfolgreich war oder nicht.
     */
    override fun updateLearningCategory(learningCategory: LearningCategory, result: (UiState<LearningCategory?>) -> Unit) {
        // Dokument der Lernkategorie in der Datenbank wird geholt
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document(learningCategory.id)
        document
            // Lernkategorie wird in der Datenbank aktualisiert
            .set(learningCategory)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(learningCategory)
                )
            }
            .addOnFailureListener {
                // Fehlermeldung wird an den Aufrufer zurückgegeben
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    /**
     * Funktion, um eine Lernkategorie aus der Datenbank zu löschen.
     * @param learningCategory Die Lernkategorie, die gelöscht werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorie erfolgreich gelöscht wurde oder nicht.
     */
    override fun deleteLearningCategory(learningCategory: LearningCategory, result: (UiState<String>) -> Unit) {
        // Zugriff auf das Lernkategorie-Dokument in der Datenbank basierend auf der Lernkategorie-ID
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document(learningCategory.id)
        // Löschen des Lernkategorie-Dokuments aus der Datenbank
        document
            .delete()
            .addOnSuccessListener {
                // Rückgabe einer Erfolgsmeldung an den Aufrufer
                result.invoke(
                    UiState.Success("Learning Category has been deleted successfully")
                )
            }
            .addOnFailureListener {
                // Rückgabe einer Fehlermeldung an den Aufrufer
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
}