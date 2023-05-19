package com.example.learnahead_prototyp.Data.Repository

import com.example.learnahead_prototyp.Data.Model.LearningCategory
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.Util.UiState

/**
 * Ein Interface, das die CRUD-Funktionalität (Erstellen, Lesen, Aktualisieren und Löschen) für Lernkategorien definiert.
 */
interface ILearnCategoryRepository {

    /**
     * Funktion zum Abrufen der Liste von Lernkategorien für den angegebenen Benutzer.
     * @param user Der Benutzer, für den die Lernkategorien abgerufen werden sollen.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Liste von Lernkategorien enthält.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun getLearningCategories(user: User?, result: (UiState<List<LearningCategory>>) -> Unit)

    /**
     * Funktion zum Hinzufügen einer neuen Lernkategorie.
     * @param learningCategory Die Lernkategorie, die hinzugefügt werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Nachricht enthält, die den Status beschreibt.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun addLearningCategory(learningCategory: LearningCategory, result: (UiState<LearningCategory?>) -> Unit)

    /**
     * Funktion zum Aktualisieren einer vorhandenen Lernkategorie.
     * @param learningCategory Die Lernkategorie, die aktualisiert werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Nachricht enthält, die den Status beschreibt.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun updateLearningCategory(learningCategory: LearningCategory, result: (UiState<LearningCategory?>) -> Unit)

    /**
     * Funktion zum Löschen einer vorhandenen Lernkategorie.
     * @param learningCategory Die Lernkategorie, die gelöscht werden soll.
     * @param result Eine Funktion, die das Ergebnis an den Aufrufer zurückgibt.
     * Das Ergebnis ist ein UiState-Objekt, das den Status der Operation sowie eine Nachricht enthält, die den Status beschreibt.
     * Der Status kann entweder Success oder Failure sein, abhängig davon, ob die Operation erfolgreich war oder nicht.
     */
    fun deleteLearningCategory(learningCategory: LearningCategory, result: (UiState<String>) -> Unit)
}