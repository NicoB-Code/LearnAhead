package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.FireStoreCollection
import com.example.learnahead_prototyp.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Klasse, die die Schnittstelle IGoalRepository implementiert und Methoden enthält, um Ziele aus der Datenbank zu holen.
 * @param database Die Firebase Firestore-Datenbank-Instanz, auf die zugegriffen werden soll.
 */
class GoalRepository(
    val database: FirebaseFirestore
) : IGoalRepository {

    val TAG: String = "GOALREPOSITORY"

    /**
     * Funktion, um Ziele aus der Datenbank zu holen.
     * @param user Der Benutzer, dessen Ziele abgerufen werden sollen.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Zielen oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zielauswahl erfolgreich war oder nicht.
     */
    override fun getGoals(user: User?, result: (UiState<List<Goal>>) -> Unit) {
        if (user == null) {
            result.invoke(UiState.Failure("User is null"))
            return
        }
        // Get the user document reference
        val userDocument = database.collection(FireStoreCollection.USER).document(user.id)
        // Fetch the user document to get the goals collection reference
        userDocument.get()
            .addOnSuccessListener { userDocument ->
                val goalsList = userDocument.toObject(User::class.java)?.goals?.toMutableList() ?: mutableListOf()
                // Update the user object with the retrieved goals
                result.invoke(UiState.Success(goalsList))
            }

            .addOnFailureListener { exception ->
                result.invoke(UiState.Failure(exception.localizedMessage))
            }
    }


    /**
     * Funktion, um ein Ziel in der Datenbank zu erstellen.
     * @param goal Das Ziel, das erstellt werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Ziel erfolgreich erstellt wurde oder nicht.
     */
    override fun addGoal(goal: Goal, result: (UiState<Goal?>) -> Unit) {
        // Erstellung eines neuen Dokuments in der GOAL-Sammlung der Datenbank
        val document = database.collection(FireStoreCollection.GOAL).document()
        // Festlegen der ID des Ziels als ID des erstellten Dokuments in der Datenbank
        goal.id = document.id
        // Hinzufügen des Ziels als Dokument zur Datenbank
        document
            .set(goal)
            .addOnSuccessListener {
                // Bei Erfolg wird eine Erfolgsmeldung an den Aufrufer zurückgegeben
                result.invoke(
                    UiState.Success(goal)
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
     * Funktion zum Aktualisieren eines Ziels in der Datenbank.
     * @param goal Das Ziel, das aktualisiert werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Meldung, ob das Ziel erfolgreich aktualisiert wurde oder nicht.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Aktualisierung erfolgreich war oder nicht.
     */
    override fun updateGoal(goal: Goal, result: (UiState<Goal?>) -> Unit) {
        // Dokument des Ziels in der Datenbank wird geholt
        val document = database.collection(FireStoreCollection.GOAL).document(goal.id)
        document
            // Ziel wird in der Datenbank aktualisiert
            .set(goal)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(goal)
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
     * Funktion, um ein Ziel aus der Datenbank zu löschen.
     * @param goal Das Ziel, das gelöscht werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Ziel erfolgreich gelöscht wurde oder nicht.
     */
    override fun deleteGoal(goal: Goal, result: (UiState<String>) -> Unit) {
        // Zugriff auf das Ziel-Dokument in der Datenbank basierend auf der Ziel-ID
        val document = database.collection(FireStoreCollection.GOAL).document(goal.id)
        // Löschen des Ziel-Dokuments aus der Datenbank
        document
            .delete()
            .addOnSuccessListener {
                // Rückgabe einer Erfolgsmeldung an den Aufrufer
                result.invoke(
                    UiState.Success("Goal has been deleted successfully")
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