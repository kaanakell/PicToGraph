package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Temperature(
    val datetime : String?,
    val pred: String?,
    val value: Int?
) : Parcelable
