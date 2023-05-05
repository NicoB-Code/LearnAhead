package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Goal(
    var id: String = "",
    var user_id: String = "",
    val description: String = "",
    @ServerTimestamp
    // WICHTIG DATE WIRD NOCH GEÄNDERT WEGEN INDEX
    val date: Date = Date()
): Parcelable
