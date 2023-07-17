package com.example.learnahead.Data.Repository

import com.example.learnahead.Data.Model.Goal
import com.example.learnahead.Data.Model.User
import com.example.learnahead.Util.FireStoreCollection
import com.example.learnahead.Util.UiState
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Eine Klasse, die das IGoalRepository-Interface implementiert und Methoden zur Verwaltung von Zielen in der Datenbank enthält.
 *
 * @param database Die Firebase Firestore-Datenbankinstanz, auf die zugegriffen werden soll.
 */
class GoalRepository(
    private val database: FirebaseFirestore
) : IGoalRepository {

    private val TAG: String = "GoalRepository"

    /**
     * Ruft die Ziele des Benutzers aus der Datenbank ab.
     *
     * @param user Der Benutzer, dessen Ziele abgerufen werden sollen.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Zielen oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zielauswahl erfolgreich war oder nicht.
     */
    override fun getGoals(user: User?, result: (UiState<List<Goal>>) -> Unit) {
        if (user == null) {
            result.invoke(UiState.Failure("Benutzer ist null."))
            return
        }

        val userDocument = database.collection(FireStoreCollection.USER).document(user.id)
        userDocument.get()
            .addOnSuccessListener { document ->
                val goalsList = document.toObject(User::class.java)?.goals?.toMutableList() ?: mutableListOf()

                val sortedList = goalsList.sortedBy { it.endDate }
                result.invoke(UiState.Success(sortedList))
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Failure(exception.localizedMessage))
            }
    }

    /**
     * Erstellt ein neues Ziel in der Datenbank.
     *
     * @param goal Das Ziel, das erstellt werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Ziel erfolgreich erstellt wurde oder nicht.
     */
    override fun addGoal(goal: Goal, result: (UiState<Goal?>) -> Unit) {
        val document = database.collection(FireStoreCollection.GOAL).document()
        goal.id = document.id
        document
            .set(goal)
            .addOnSuccessListener {
                result.invoke(UiState.Success(goal))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    /**
     * Aktualisiert ein Ziel in der Datenbank.
     *
     * @param goal Das Ziel, das aktualisiert werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Ziel erfolgreich aktualisiert wurde oder nicht.
     */
    override fun updateGoal(goal: Goal, result: (UiState<Goal?>) -> Unit) {
        val document = database.collection(FireStoreCollection.GOAL).document(goal.id)
        document
            .set(goal)
            .addOnSuccessListener {
                result.invoke(UiState.Success(goal))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    /**
     * Löscht ein Ziel aus der Datenbank.
     *
     * @param goal Das Ziel, das gelöscht werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Ziel erfolgreich gelöscht wurde oder nicht.
     */
    override fun deleteGoal(goal: Goal, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.GOAL).document(goal.id)
        document
            .delete()
            .addOnSuccessListener {
                result.invoke(UiState.Success("Ziel wurde erfolgreich gelöscht."))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }
}