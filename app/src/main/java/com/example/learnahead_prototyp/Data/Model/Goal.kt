package com.example.learnahead_prototyp.Data.Model

import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Goal(
    val id: String,
    val description: String,
    @ServerTimestamp
    val beginDate: Date,
    @ServerTimestamp
    val endDate: Date
)
