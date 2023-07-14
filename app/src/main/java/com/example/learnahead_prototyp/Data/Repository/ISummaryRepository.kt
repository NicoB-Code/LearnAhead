package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Summary
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

/**
 * Das Interface ISummaryRepository definiert Methoden zur Verwaltung von Zusammenfassungen in der Datenbank.
 */
interface ISummaryRepository {

    /**
     * Ruft die Zusammenfassungen für einen bestimmten Benutzer und eine bestimmte Lernkategorie aus der Datenbank ab.
     *
     * @param user Der Benutzer, dessen Zusammenfassungen abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, zu der die Zusammenfassungen gehören sollen.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Zusammenfassungen oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zusammenfassungen erfolgreich abgerufen wurden oder nicht.
     */
    fun getSummaries(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Summary>>) -> Unit)

    /**
     * Fügt eine Zusammenfassung zur Datenbank hinzu.
     *
     * @param summary Die Zusammenfassung, die hinzugefügt werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zusammenfassung erfolgreich hinzugefügt wurde oder nicht.
     */
    fun addSummary(summary: Summary, result: (UiState<Summary?>) -> Unit)

    /**
     * Aktualisiert eine Zusammenfassung in der Datenbank.
     *
     * @param summary Die Zusammenfassung, die aktualisiert werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zusammenfassung erfolgreich aktualisiert wurde oder nicht.
     */
    fun updateSummary(summary: Summary, result: (UiState<Summary?>) -> Unit)

    /**
     * Löscht eine Zusammenfassung aus der Datenbank.
     *
     * @param summary Die Zusammenfassung, die gelöscht werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Zusammenfassung erfolgreich gelöscht wurde oder nicht.
     */
    fun deleteSummary(summary: Summary, result: (UiState<String>) -> Unit)
}