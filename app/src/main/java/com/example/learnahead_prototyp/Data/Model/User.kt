package com.example.learnahead_prototyp.Data.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Eine Datenklasse, die einen Benutzer in der App darstellt.
 * @property id Die eindeutige ID des Benutzers.
 * @property first_name Der Vorname des Benutzers.
 * @property last_name Der Nachname des Benutzers.
 * @property job_title Der Jobtitel des Benutzers.
 * @property email Die E-Mail-Adresse des Benutzers.
 */
data class User(
    var id: String = "",
    val username: String = "",
    val currentPoints: Int = 0,
    val learningStreak: Int = 0,
    val email: String = "",
    @ServerTimestamp
    val registerDate: Date = Date()
)