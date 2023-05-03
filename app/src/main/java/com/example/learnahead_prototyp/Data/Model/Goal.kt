package com.example.learnahead_prototyp.Data.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Goal(
    var id: String = "",
    val description: String = "",
    @ServerTimestamp
    val endDate: Date = Date()
)
