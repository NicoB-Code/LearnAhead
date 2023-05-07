package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
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
class GoalRepository(
    val database: FirebaseFirestore
) : IGoalRepository {

    /**
     * Funktion, um Ziele aus der Datenbank zu holen.
     * @param user Der Benutzer, dessen Ziele abgerufen werden sollen.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Zielen oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zielauswahl erfolgreich war oder nicht.
     */
    override fun getGoals(user: User?, result: (UiState<List<Goal>>) -> Unit) {
        // Auswahl aller Ziele des Benutzers aus der Datenbank, geordnet nach dem Datum, absteigend
        // ACHTUNG: Ein Index in Firebase muss erstellt werden, um die orderBy-Funktion zu nutzen. Fehler würden im LogCat protokolliert werden
        database.collection(FireStoreCollection.GOAL)
            // Filtern der Dokumente auf die, die zum übergebenen Benutzer gehören
            .whereEqualTo(FireStoreDocumentField.USER_ID, user?.id)
            // Sortieren der Dokumente nach dem Datum, absteigend
            .orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
            // Abfrage der ausgewählten Dokumente
            .get()
            // Falls die Abfrage erfolgreich war
            .addOnSuccessListener {
                // Erstellen einer leeren Liste für die abgerufenen Ziele
                val goals = arrayListOf<Goal>()
                // Schleife über die abgerufenen Dokumente
                for (document in it) {
                    // Umwandeln des Dokuments in ein Goal-Objekt
                    val goal = document.toObject(Goal::class.java)
                    // Hinzufügen des Goal-Objekts zur goals-Liste
                    goals.add(goal)
                }
                // Erfolgreiche Auswahl der Ziele wird an den Aufrufer zurückgegeben
                result.invoke(UiState.Success(goals))
            }
            .addOnFailureListener {
                // Bei Fehlern wird eine Fehlermeldung an den Aufrufer zurückgegeben
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }


    /**
     * Funktion, um ein Ziel in der Datenbank zu erstellen.
     * @param goal Das Ziel, das erstellt werden soll.
     * @param result Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob das Ziel erfolgreich erstellt wurde oder nicht.
     */
    override fun addGoal(goal: Goal, result: (UiState<String>) -> Unit) {
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
                    UiState.Success("Goal has been created successfully")
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
    override fun updateGoal(goal: Goal, result: (UiState<String>) -> Unit) {
        // Dokument des Ziels in der Datenbank wird geholt
        val document = database.collection(FireStoreCollection.GOAL).document(goal.id)
        document
            // Ziel wird in der Datenbank aktualisiert
            .set(goal)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Goal has been update successfully")
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