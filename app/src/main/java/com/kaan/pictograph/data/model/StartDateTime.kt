package com.kaan.pictograph.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class StartDateTime(
    val startTime: String?,
    val startDate: String
) : Parcelable
