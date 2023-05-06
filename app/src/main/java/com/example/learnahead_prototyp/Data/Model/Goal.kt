package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Eine Datenklasse, die ein Lernziel in der App darstellt.
 *
 * @property id Die eindeutige ID des Lernziels.
 * @property user_id Die ID des Benutzers, dem das Lernziel gehört.
 * @property description Die Beschreibung des Lernziels.
 * @property date Das Datum, an dem das Lernziel erstellt oder aktualisiert wurde. Es wird automatisch
 * vom Server generiert und mit dem "@ServerTimestamp" Annotation markiert.
 */

/** Bzgl. Parcelize:
 * ist eine Annotation von kotlinx.android.parcel, die automatisch die Implementierung der Parcelable-Schnittstelle
 * für die annotierte Klasse generiert. Parcelable ist eine Schnittstelle, die dazu dient, Daten zwischen verschiedenen
 * Komponenten innerhalb der Android-App zu übertragen. Die Verwendung von Parcelable kann eine bessere Leistung bieten als Serializable.
 */
@Parcelize
data class Goal(
    var id: String = "",
    var user_id: String = "",
    val description: String = "",
    @ServerTimestamp
    // WICHTIG DATE WIRD NOCH GEÄNDERT WEGEN INDEX
    val date: Date = Date()
): Parcelable
