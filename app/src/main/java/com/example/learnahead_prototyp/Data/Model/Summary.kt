package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Summary(
    var id: String = "",
    val name: String = "",
    var content: String = "",
    @ServerTimestamp
    val createDate: Date = Date(),
    ) : Parcelable