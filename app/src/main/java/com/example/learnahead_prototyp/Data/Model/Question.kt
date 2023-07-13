package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import com.example.learnahead_prototyp.Util.QuestionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    var id: String = "",
    val question: String = "",
    val answer: String = "",
    val type: QuestionType = QuestionType.IndexCard,
    var lastTest: Boolean? = null,
    var wrongCounter: Int = 0,
    var tags: MutableList<Tag> = mutableListOf(),
    ) : Parcelable