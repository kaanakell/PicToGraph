package com.kaan.pictograph.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Temperature(
    val datetime : String?,
    val sensor: String?,
    val pred: String?,
    val value: Float?
):Parcelable

