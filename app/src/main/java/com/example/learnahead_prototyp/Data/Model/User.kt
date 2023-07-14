package com.example.learnahead_prototyp.Data.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Eine Datenklasse, die einen Benutzer in der App darstellt.
 *
 * @property id Die eindeutige ID des Benutzers.
 * @property username Der Benutzername des Benutzers.
 * @property password Das Passwort des Benutzers.
 * @property currentPoints Die aktuelle Punktzahl des Benutzers.
 * @property learningStreak Die aktuelle Lernserie des Benutzers.
 * @property achievedGoals Die Anzahl der erreichten Lernziele des Benutzers.
 * @property email Die E-Mail-Adresse des Benutzers.
 * @property registerDate Das Datum, an dem der Benutzer registriert wurde. Es wird automatisch vom Server generiert
 * und mit der Annotation "@ServerTimestamp" markiert.
 * @property lastLogin Das Datum des letzten Logins des Benutzers.
 * @property profileImageUrl Die URL des Profilbilds des Benutzers.
 * @property goals Eine Liste von Lernzielen, die dem Benutzer zugeordnet sind.
 * @property learningCategories Eine Liste von Lernkategorien, die dem Benutzer zugeordnet sind.
 * @property summaries Eine Liste von Zusammenfassungen, die dem Benutzer zugeordnet sind.
 */
data class User(
    var id: String = "",
    val username: String = "",
    val password: String = "",
    var currentPoints: Int = 0,
    var learningStreak: Int = 0,
    var achievedGoals: Int = 0,
    val email: String = "",
    @ServerTimestamp
    val registerDate: Date = Date(),
    var lastLogin: Date = Date(),
    val profileImageUrl: String = "",
    var goals: MutableList<Goal> = mutableListOf(),
    var learningCategories: MutableList<LearningCategory> = mutableListOf(),
    var summaries: MutableList<Summary> = mutableListOf()
)