package com.example.learnahead.Data.Model

import android.os.Parcelable
import com.example.learnahead.Util.GoalStatus
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Eine Datenklasse, die ein Lernziel in der App darstellt.
 *
 * @property id Die eindeutige ID des Lernziels.
 * @property name Der Name des Lernziels.
 * @property description Die Beschreibung des Lernziels.
 * @property startDate Das Datum, an dem das Lernziel erstellt oder aktualisiert wurde. Es wird automatisch
 * vom Server generiert und mit der Annotation "@ServerTimestamp" markiert.
 * @property endDate Das Enddatum des Lernziels. Es wird automatisch vom Server generiert und mit der Annotation
 * "@ServerTimestamp" markiert.
 * @property lastLearned Das Datum, an dem das Lernziel zuletzt bearbeitet wurde. Es kann null sein, falls das Lernziel
 * noch nicht bearbeitet wurde.
 * @property status Der Status des Lernziels (ToDo, InProgress, Completed).
 */
@Parcelize
data class Goal(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    @ServerTimestamp
    val startDate: Date = Date(),
    @ServerTimestamp
    val endDate: Date = Date(),
    val lastLearned: Date? = null,
    val status: GoalStatus = GoalStatus.ToDo
) : Parcelable