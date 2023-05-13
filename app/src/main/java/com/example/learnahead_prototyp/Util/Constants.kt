package com.example.learnahead_prototyp.Util

/**
 * Das sind Tabellen in Firebase
 */
object FireStoreCollection {
    val GOAL = "goal"
    val USER = "user"
}

/**
 * Das sind bestimmte Tabellenfelder in Firebase
 */
object FireStoreDocumentField {
    // WICHTIG DATE WIRD NOCH ABGEÄNDERT WEGEN INDEX
    val END_DATE = "endDate"
    val USER_ID = "user_id"
}

/**
 * Das ist für Session Handling
 */
object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
}