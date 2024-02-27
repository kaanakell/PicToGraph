package com.kaan.pictograph.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EndDateTime(
    val endTime: String?,
    val endDate: String
) : Parcelable
