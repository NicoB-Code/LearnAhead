package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    var id: String = "",
    var name: String = "",
) : Parcelable