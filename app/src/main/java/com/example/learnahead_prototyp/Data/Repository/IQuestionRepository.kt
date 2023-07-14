package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.Question
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

/**
 * Das Interface IQuestionRepository definiert Methoden, um Fragen in der Datenbank zu verwalten.
 */
interface IQuestionRepository {

    /**
     * Ruft die Fragen für einen bestimmten Benutzer und eine bestimmte Lernkategorie aus der Datenbank ab.
     *
     * @param user Der Benutzer, dessen Fragen abgerufen werden sollen.
     * @param learningCategory Die Lernkategorie, zu der die Fragen gehören sollen.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Liste von Fragen oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Fragen erfolgreich abgerufen wurden oder nicht.
     */
    fun getQuestions(user: User?, learningCategory: LearningCategory?, result: (UiState<List<Question>>) -> Unit)

    /**
     * Fügt eine Frage zur Datenbank hinzu.
     *
     * @param question Die Frage, die hinzugefügt werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Frage erfolgreich hinzugefügt wurde oder nicht.
     */
    fun addQuestion(question: Question, result: (UiState<Question?>) -> Unit)

    /**
     * Aktualisiert eine Frage in der Datenbank.
     *
     * @param question Die Frage, die aktualisiert werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Frage erfolgreich aktualisiert wurde oder nicht.
     */
    fun updateQuestion(question: Question, result: (UiState<Question?>) -> Unit)

    /**
     * Löscht eine Frage aus der Datenbank.
     *
     * @param question Die Frage, die gelöscht werden soll.
     * @param result Die Funktion zur Rückgabe des Ergebnisses an den Aufrufer.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation enthält sowie eine Erfolgsmeldung oder eine Fehlermeldung, je nach Ergebnis der Operation.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Frage erfolgreich gelöscht wurde oder nicht.
     */
    fun deleteQuestion(question: Question, result: (UiState<String>) -> Unit)
}