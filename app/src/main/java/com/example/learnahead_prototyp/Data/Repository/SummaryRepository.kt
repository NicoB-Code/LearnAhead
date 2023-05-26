package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore


/**
 * Klasse, die die Schnittstelle ISummaryRepository implementiert und Methoden enthält, um Zusammenfassungen aus der Datenbank zu holen, hinzuzufügen, zu aktualisieren und zu löschen.
 * @param database Die Firebase Firestore-Datenbank-Instanz, auf die zugegriffen werden soll.
 */
class SummaryRepository(
    val database: FirebaseFirestore
) : ISummaryRepository {

    /**
     * Funktion, um Zusammenfassungen für eine bestimmte Lernkategorie und einen Benutzer aus der Datenbank zu holen.
     * @param user Der Benutzer, dessen Zusammenfassungen abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, für die Zusammenfassungen abgerufen werden sollen.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Zusammenfassungen oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob es erfolgreich war oder nicht.
     */
    override fun getSummaries(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Summary>>) -> Unit) {
        // Überprüfen, ob learningCategory null ist
        if (learningCategory == null) {
            result(UiState.Failure("learningCategory is null"))
            return
        }

        // Überprüfen, ob user null ist
        if (user == null) {
            result(UiState.Failure("user is null"))
            return
        }

        // Dokumentreferenz des Benutzers holen
        val userDocumentRef = database.collection(FireStoreCollection.USER).document(user.id)

        // Benutzerdokument abrufen
        val learningCategoriesTask = userDocumentRef.get().continueWith { task ->
            task.result?.toObject(User::class.java)?.learningCategories
        }

        // Zusammenfassungen für die Ziel-Lernkategorie holen
        val summariesTask = learningCategoriesTask.continueWith { task ->
            val learningCategories = task.result
            if (learningCategories.isNullOrEmpty()) {
                throw Exception("Learning categories are null or empty")
            }
            val targetLearningCategory = learningCategories.find { it.id == learningCategory.id }
                ?: throw Exception("Learning category not found")
            targetLearningCategory.summaries
        }

        // Ergebnis zurückgeben
        summariesTask.addOnSuccessListener { summaries ->
            result(UiState.Success(summaries))
        }.addOnFailureListener { exception ->
            result(UiState.Failure(exception.localizedMessage))
        }
    }

    /**
     * Funktion, um eine Zusammenfassung in der Datenbank hinzuzufügen.
     * @param summary Die Zusammenfassung, die hinzugefügt werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zusammenfassung erfolgreich hinzugefügt wurde oder nicht.
     */
    override fun addSummary(summary: Summary, result: (UiState<Summary?>) -> Unit) {
        // Neues Dokument in der SUMMARY-Sammlung der Datenbank erstellen
        val document = database.collection(FireStoreCollection.SUMMARY).document()

        // ID der Zusammenfassung als ID des erstellten Dokuments in der Datenbank festlegen
        summary.id = document.id

        // Zusammenfassung als Dokument zur Datenbank hinzufügen
        document.set(summary).addOnSuccessListener {
            // Bei Erfolg wird eine Erfolgsmeldung an den Aufrufer zurückgegeben
            result(UiState.Success(summary))
        }.addOnFailureListener {
            // Bei Fehlern wird eine Fehlermeldung an den Aufrufer zurückgegeben
            result(UiState.Failure(it.localizedMessage))
        }
    }

    /**
     * Funktion zum Aktualisieren einer Zusammenfassung in der Datenbank.
     * @param summary Die Zusammenfassung, die aktualisiert werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Meldung, ob die Zusammenfassung erfolgreich aktualisiert wurde oder nicht.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Aktualisierung erfolgreich war oder nicht.
     */
    override fun updateSummary(summary: Summary, result: (UiState<Summary?>) -> Unit) {
        // Dokument der Zusammenfassung in der Datenbank holen
        val document = database.collection(FireStoreCollection.SUMMARY).document(summary.id)

        // Zusammenfassung in der Datenbank aktualisieren
        document.apply {
            set(summary)
        }.addOnSuccessListener {
            result(UiState.Success(summary))
        }.addOnFailureListener {
            // Fehlermeldung an den Aufrufer zurückgeben
            result(UiState.Failure(it.localizedMessage))
        }
    }

    /**
     * Funktion, um eine Zusammenfassung aus der Datenbank zu löschen.
     * @param summary Die Zusammenfassung, die gelöscht werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zusammenfassung erfolgreich gelöscht wurde oder nicht.
     */
    override fun deleteSummary(summary: Summary, result: (UiState<String>) -> Unit) {
        // Zugriff auf das Zusammenfassungs-Dokument in der Datenbank basierend auf der Zusammenfassungs-ID
        val document = database.collection(FireStoreCollection.SUMMARY).document(summary.id)

        // Zusammenfassungs-Dokument aus der Datenbank löschen
        document.delete().addOnSuccessListener {
            // Erfolgsmeldung an den Aufrufer zurückgeben
            result(UiState.Success("Summary has been deleted successfully"))
        }.addOnFailureListener {
            // Fehlermeldung an den Aufrufer zurückgeben
            result(UiState.Failure(it.localizedMessage))
        }
    }
}