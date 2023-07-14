package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Eine Datenklasse, die einen Test in der App darstellt.
 *
 * @property id Die eindeutige ID des Tests.
 * @property name Der Name des Tests.
 * @property questions Eine Liste von Fragen, die mit diesem Test verknüpft sind.
 * @property createDate Das Datum, an dem der Test erstellt wurde. Es wird automatisch vom Server generiert
 * und mit der Annotation "@ServerTimestamp" markiert.
 * @property modifiedDate Das Datum, an dem der Test zuletzt bearbeitet wurde. Es wird automatisch vom Server generiert
 * und mit der Annotation "@ServerTimestamp" markiert.
 */
@Parcelize
data class Test(
    var id: String = "",
    val name: String = "",
    var questions: MutableList<Question> = mutableListOf(),
    @ServerTimestamp
    val createDate: Date = Date(),
    @ServerTimestamp
    val modifiedDate: Date = Date()
) : Parcelable