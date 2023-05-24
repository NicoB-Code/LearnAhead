package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Klasse, die die Schnittstelle IGoalRepository implementiert und Methoden enthält, um Ziele aus der Datenbank zu holen.
 * @param database Die Firebase Firestore-Datenbank-Instanz, auf die zugegriffen werden soll.
 */
class SummaryRepository(
    val database: FirebaseFirestore
) : ISummaryRepository {

    /**
     * Funktion, um Ziele aus der Datenbank zu holen.
     * @param user Der Benutzer, dessen Lernkategorie abgerufen werden sollen.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Lernkategorien oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob es erfolgreich war oder nicht.
     */
    override fun getSummaries(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Summary>>) -> Unit) {
        if (learningCategory == null) {
            result.invoke(UiState.Failure("learningCategory is null"))
            return
        }

        if (user == null) {
            result.invoke(UiState.Failure("user is null"))
            return
        }

        val userDocumentRef = database.collection(FireStoreCollection.USER).document(user.id)

        userDocumentRef.get()
            .addOnSuccessListener { userDocument ->
                val learningCategories = userDocument.toObject(User::class.java)?.learningCategories
                if (learningCategories.isNullOrEmpty()) {
                    result.invoke(UiState.Failure("Learning categories are null or empty"))
                    return@addOnSuccessListener
                }

                val targetLearningCategory = learningCategories.find { it.id == learningCategory.id }
                if (targetLearningCategory == null) {
                    result.invoke(UiState.Failure("Learning category not found"))
                    return@addOnSuccessListener
                }

                val summaries = targetLearningCategory.summaries
                result.invoke(UiState.Success(summaries))
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
    override fun addSummary(summary: Summary, result: (UiState<Summary?>) -> Unit) {
        // Erstellung eines neuen Dokuments in der GOAL-Sammlung der Datenbank
        val document = database.collection(FireStoreCollection.SUMMARY).document()
        // Festlegen der ID des Ziels als ID des erstellten Dokuments in der Datenbank
        summary.id = document.id
        // Hinzufügen des Ziels als Dokument zur Datenbank
        document
            .set(summary)
            .addOnSuccessListener {
                // Bei Erfolg wird eine Erfolgsmeldung an den Aufrufer zurückgegeben
                result.invoke(
                    UiState.Success(summary)
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
    override fun updateSummary(summary: Summary, result: (UiState<Summary?>) -> Unit) {
        // Dokument der Lernkategorie in der Datenbank wird geholt
        val document = database.collection(FireStoreCollection.SUMMARY).document(summary.id)
        document
            // Lernkategorie wird in der Datenbank aktualisiert
            .set(summary)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(summary)
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
     * @param summary Die Lernkategorie, die gelöscht werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Lernkategorie erfolgreich gelöscht wurde oder nicht.
     */
    override fun deleteSummary(summary: Summary, result: (UiState<String>) -> Unit) {
        // Zugriff auf das Lernkategorie-Dokument in der Datenbank basierend auf der Lernkategorie-ID
        val document = database.collection(FireStoreCollection.SUMMARY).document(summary.id)
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