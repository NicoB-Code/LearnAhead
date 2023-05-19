package com.example.learnahead_prototyp.Data.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Test(
    var id: String = "",
    val name: String = "",
) : Parcelable