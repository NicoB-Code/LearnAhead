package com.example.learnahead_prototyp.Data.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Eine Datenklasse, die einen Benutzer in der App darstellt.
 * @property id Die eindeutige ID des Benutzers.
 * @property email Die E-Mail-Adresse des Benutzers.
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
) {

}