package com.example.learnahead_prototyp.Util

/**
 * Das sind Tabellen in Firebase
 */
object FireStoreCollection {
    val TAG = "tag"
    val QUESTION = "question"
    val SUMMARY = "summary"
    val GOAL = "goal"
    val LEARNING_CATEGORY = "learning_category"
    val USER = "user"
}

/**
 * Das sind bestimmte Tabellenfelder in Firebase
 */
object FireStoreDocumentField {
    // WICHTIG DATE WIRD NOCH ABGEÄNDERT WEGEN INDEX
    val END_DATE = "endDate"
    val NAME = "name"
    val USER_ID = "user_id"
}

/**
 * Das ist für Session Handling
 */
object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
}