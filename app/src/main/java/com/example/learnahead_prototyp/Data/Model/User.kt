package com.example.learnahead_prototyp.Data.Model

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
    val first_name: String = "",
    val last_name: String = "",
    val job_title: String = "",
    val email: String = "",
)