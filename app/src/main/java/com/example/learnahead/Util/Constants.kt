package com.example.learnahead.Util

/**
 * Enthält die Namen der Tabellen in Firebase.
 */
object FireStoreCollection {
    /**
     * Tabelle "test"
     */
    val TEST = "test"

    /**
     * Tabelle "tag"
     */
    val TAG = "tag"

    /**
     * Tabelle "question"
     */
    val QUESTION = "question"

    /**
     * Tabelle "summary"
     */
    val SUMMARY = "summary"

    /**
     * Tabelle "goal"
     */
    val GOAL = "goal"

    /**
     * Tabelle "learning_category"
     */
    val LEARNING_CATEGORY = "learning_category"

    /**
     * Tabelle "user"
     */
    val USER = "user"
}

/**
 * Enthält eine Liste von Lerntipps.
 */
object Lerntipps {
    /**
     * Liste von Lerntipps.
     */
    val LERNTIPPS_LISTE = listOf(
        "Verteiltes Lernen: Anstatt sich intensiv auf ein Thema zu konzentrieren, ist es oft effektiver, das Lernen über einen längeren Zeitraum zu verteilen. Diese Methode hilft, das Erinnern von Informationen zu verbessern.",
        "Interleaved Practice: Abwechselndes Üben verschiedener Fähigkeiten oder Themen innerhalb einer Lerneinheit kann dazu beitragen, das Verständnis und die Erinnerung zu verbessern.",
        "Selbsterklärung: Wenn Lernende die Konzepte, die sie lernen, erklären oder unterrichten, kann dies dazu beitragen, ihr Verständnis zu vertiefen.",
        "Elaboratives Interrogieren: Frage dich selbst \"Warum?\" und \"Wie?\" während des Lernprozesses. Diese Art von kritischem Denken kann das Verständnis vertiefen.",
        "Wiederholtes Testen: Selbsttests oder Quizze können helfen, das Langzeitgedächtnis zu verbessern. Wiederholtes Testen nach einigen Tagen kann besonders effektiv sein.",
        "Geschlafen wird nicht gelernt, aber gefestigt: Schlaf ist wichtig für das Gedächtnis und das Lernen. Stelle sicher, dass du genug Schlaf bekommst, besonders nach einer Lerneinheit.",
        "Verknüpfung mit Vorwissen: Versuche, neue Informationen mit etwas zu verbinden, das du bereits weißt. Diese Assoziationen können helfen, das Gedächtnis zu verbessern.",
        "Gesunde Ernährung und Bewegung: Beide haben einen direkten Einfluss auf das Gehirn und können die kognitiven Funktionen, einschließlich des Lernens und des Gedächtnisses, verbessern.",
        "Mind Maps erstellen: Diese helfen, Verbindungen zwischen verschiedenen Konzepten zu visualisieren, was das Verständnis und die Erinnerung verbessern kann.",
        "Meditation und Achtsamkeitspraxis: Diese können die Konzentration verbessern und Stress reduzieren, was das Lernen effektiver machen kann."
    )
}

/**
 * Enthält die Namen bestimmter Felder in Firebase-Dokumenten.
 */
object FireStoreDocumentField {
    /**
     * Das Feld "endDate" in Firebase-Dokumenten.
     */
    val END_DATE = "endDate"

    /**
     * Das Feld "name" in Firebase-Dokumenten.
     */
    val NAME = "name"

    /**
     * Das Feld "user_id" in Firebase-Dokumenten.
     */
    val USER_ID = "user_id"
}

/**
 * Enthält Konstanten für das Session Handling.
 */
object SharedPrefConstants {
    /**
     * Der Name des Shared Preferences.
     */
    val LOCAL_SHARED_PREF = "local_shared_pref"

    /**
     * Der Key für die Benutzersitzung in den Shared Preferences.
     */
    val USER_SESSION = "user_session"
}