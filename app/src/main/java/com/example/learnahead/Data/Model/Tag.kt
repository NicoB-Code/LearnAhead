package com.example.learnahead.Data.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Eine Datenklasse, die einen Tag in der App darstellt.
 *
 * @property id Die eindeutige ID des Tags.
 * @property name Der Name des Tags.
 */
@Parcelize
data class Tag(
    var id: String = "",
    var name: String = ""
) : Parcelable