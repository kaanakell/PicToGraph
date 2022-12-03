package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Times(
    val startTime: String?,
    val endTime: String?
) : Parcelable
