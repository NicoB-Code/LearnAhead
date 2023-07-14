package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.example.learnahead_prototyp.Util.QuestionType
import kotlinx.parcelize.Parcelize

/**
 * Eine Datenklasse, die eine Frage in der App darstellt.
 *
 * @property id Die eindeutige ID der Frage.
 * @property question Der Fragetext.
 * @property answer Die Antwort auf die Frage.
 * @property type Der Fragetyp (IndexCard, weitere FragenTypen können noch folgen).
 * @property lastTest Gibt an, ob die Frage zuletzt richtig oder falsch beantwortet wurde.
 * @property wrongCounter Die Anzahl der falschen Antworten auf die Frage.
 * @property tags Eine Liste von Tags, die mit der Frage verknüpft sind.
 */
@Parcelize
data class Question(
    var id: String = "",
    val question: String = "",
    val answer: String = "",
    val type: QuestionType = QuestionType.IndexCard,
    var lastTest: Boolean? = null,
    var wrongCounter: Int = 0,
    var tags: MutableList<Tag> = mutableListOf()
) : Parcelable