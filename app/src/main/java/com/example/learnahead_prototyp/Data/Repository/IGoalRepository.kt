package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.Goal
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

/**
 * Ein Interface, das die CRUD-Funktionalität (Erstellen, Lesen, Aktualisieren und Löschen) für Ziele definiert.
 */
interface IGoalRepository {

    /**
     * Funktion zum Abrufen der Liste von Zielen für den angegebenen Benutzer.
     * @param user Der Benutzer, für den die Ziele abgerufen werden sollen.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Liste von Zielen enthält.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun getGoals(user: User?, result: (UiState<List<Goal>>) -> Unit)

    /**
     * Funktion zum Hinzufügen eines neuen Ziels.
     * @param goal Das Ziel, das hinzugefügt werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Nachricht enthält, die den Status beschreibt.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun addGoal(goal: Goal, result: (UiState<String>) -> Unit)

    /**
     * Funktion zum Aktualisieren eines vorhandenen Ziels.
     * @param goal Das Ziel, das aktualisiert werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Nachricht enthält, die den Status beschreibt.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun updateGoal(goal: Goal, result: (UiState<String>) -> Unit)

    /**
     * Funktion zum Löschen eines vorhandenen Ziels.
     * @param goal Das Ziel, das gelöscht werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Nachricht enthält, die den Status beschreibt.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun deleteGoal(goal: Goal, result: (UiState<String>) -> Unit)
}