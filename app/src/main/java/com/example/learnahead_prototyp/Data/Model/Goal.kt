package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Goal(
    var id: String = "",
    val description: String = "",
    @ServerTimestamp
    val endDate: Date = Date()
): Parcelable
