package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Test(
    var id: String = "",
    val name: String = "",
    var questions: MutableList<Question> = mutableListOf(),
    @ServerTimestamp
    // WICHTIG DATE WIRD NOCH GEÄNDERT WEGEN INDEX
    val createDate: Date = Date(),
    @ServerTimestamp
    val modifiedDate: Date = Date(),
    ) : Parcelable