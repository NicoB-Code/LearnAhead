package com.example.learnahead_prototyp.Util

/**
 * Das sind Tabellen in Firebase
 */
object FireStoreCollection {
    val TEST = "test"
    val TAG = "tag"
    val QUESTION = "question"
    val SUMMARY = "summary"
    val GOAL = "goal"
    val LEARNING_CATEGORY = "learning_category"
    val USER = "user"
}
object Lerntipps {
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