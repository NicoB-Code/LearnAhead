package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.FireStoreDocumentField
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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
        database.collection(FireStoreCollection.LEARNING_CATEGORY)
            // Filtern der Dokumente auf die, die zum übergebenen Benutzer gehören
            .whereEqualTo(FireStoreDocumentField.USER_ID, user?.id)
            // Sortieren der Dokumente nach dem Datum, absteigend
            .orderBy(FireStoreDocumentField.NAME, Query.Direction.ASCENDING)
            // Abfrage der ausgewählten Dokumente
            .get()
            // Falls die Abfrage erfolgreich war
            .addOnSuccessListener {
                // Erstellen einer leeren Liste für die abgerufenen Ziele
                val learningCategories = arrayListOf<LearningCategory>()
                // Schleife über die abgerufenen Dokumente
                for (document in it) {
                    // Umwandeln des Dokuments in ein LearningCategory-Objekt
                    val learningCategory = document.toObject(LearningCategory::class.java)
                    // Hinzufügen des LearningCategory-Objekts zur goals-Liste
                    learningCategories.add(learningCategory)
                }
                // Erfolgreiche Auswahl der Lernkategorien wird an den Aufrufer zurückgegeben
                result.invoke(UiState.Success(learningCategories))
            }
            .addOnFailureListener {
                // Bei Fehlern wird eine Fehlermeldung an den Aufrufer zurückgegeben
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }


    /**
     * Funktion, um ein Ziel in der Datenbank zu erstellen.
     * @param learningCategory Die Lernkategorie, die erstellt werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorie erfolgreich erstellt wurde oder nicht.
     */
    override fun addLearningCategory(learningCategory: LearningCategory, result: (UiState<String>) -> Unit) {
        // Erstellung eines neuen Dokuments in der Lernkategorie-Sammlung der Datenbank
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document()
        // Festlegen der ID der Lernkategorie als ID des erstellten Dokuments in der Datenbank
        learningCategory.id = document.id
        // Hinzufügen der Lernkategorie als Dokument zur Datenbank
        document
            .set(learningCategory)
            .addOnSuccessListener {
                // Bei Erfolg wird eine Erfolgsmeldung an den Aufrufer zurückgegeben
                result.invoke(
                    UiState.Success("Learning Category has been created successfully")
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
    override fun updateLearningCategory(learningCategory: LearningCategory, result: (UiState<String>) -> Unit) {
        // Dokument der Lernkategorie in der Datenbank wird geholt
        val document = database.collection(FireStoreCollection.LEARNING_CATEGORY).document(learningCategory.id)
        document
            // Lernkategorie wird in der Datenbank aktualisiert
            .set(learningCategory)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Learning Category has been update successfully")
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