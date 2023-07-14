package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Eine Datenklasse, die eine Zusammenfassung in der App darstellt.
 *
 * @property id Die eindeutige ID der Zusammenfassung.
 * @property name Der Name der Zusammenfassung.
 * @property content Der Inhalt der Zusammenfassung.
 * @property createDate Das Datum, an dem die Zusammenfassung erstellt wurde. Es wird automatisch vom Server generiert
 * und mit der Annotation "@ServerTimestamp" markiert.
 */
@Parcelize
data class Summary(
    var id: String = "",
    val name: String = "",
    var content: String = "",
    @ServerTimestamp
    val createDate: Date = Date()
) : Parcelable