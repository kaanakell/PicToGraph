package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Times(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
) : Parcelable
