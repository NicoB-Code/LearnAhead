package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Eine Datenklasse, die eine Lernkategorie in der App darstellt.
 *
 * @property id Die eindeutige ID der Lernkategorie.
 * @property name Der Name der Lernkategorie.
 * @property createDate Das Datum, an dem die Lernkategorie erstellt wurde. Es wird automatisch vom Server generiert
 * und mit der Annotation "@ServerTimestamp" markiert.
 * @property questions Eine Liste von Fragen, die mit dieser Lernkategorie verknüpft sind.
 * @property summaries Eine Liste von Zusammenfassungen, die mit dieser Lernkategorie verknüpft sind.
 * @property tests Eine Liste von Tests, die mit dieser Lernkategorie verknüpft sind.
 * @property relatedLearningGoal Das zugehörige Lernziel, falls vorhanden.
 */
@Parcelize
data class LearningCategory(
    var id: String = "",
    val name: String = "",
    @ServerTimestamp
    val createDate: Date = Date(),
    var questions: MutableList<Question> = mutableListOf(),
    var summaries: MutableList<Summary> = mutableListOf(),
    var tests: MutableList<Test> = mutableListOf(),
    var relatedLearningGoal: Goal? = null
) : Parcelable