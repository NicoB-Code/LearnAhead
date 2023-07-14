package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Test
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

/**
 * Das Interface ITestRepository definiert Methoden zur Verwaltung von Tests in der Datenbank.
 */
interface ITestRepository {
    /**
     * Ruft die Tests für einen bestimmten Benutzer und eine bestimmte Lernkategorie aus der Datenbank ab.
     *
     * @param user Der Benutzer, dessen Tests abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, zu der die Tests gehören sollen.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Tests oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Tests erfolgreich abgerufen wurden oder nicht.
     */
    fun getTests(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Test>>) -> Unit)

    /**
     * Fügt einen Test zur Datenbank hinzu.
     *
     * @param test Der Test, der hinzugefügt werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob der Test erfolgreich hinzugefügt wurde oder nicht.
     */
    fun addTest(test: Test, result: (UiState<Test?>) -> Unit)

    /**
     * Aktualisiert einen Test in der Datenbank.
     *
     * @param test Der Test, der aktualisiert werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob der Test erfolgreich aktualisiert wurde oder nicht.
     */
    fun updateTest(test: Test, result: (UiState<Test?>) -> Unit)

    /**
     * Löscht einen Test aus der Datenbank.
     *
     * @param test Der Test, der gelöscht werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob der Test erfolgreich gelöscht wurde oder nicht.
     */
    fun deleteTest(test: Test, result: (UiState<String>) -> Unit)
}