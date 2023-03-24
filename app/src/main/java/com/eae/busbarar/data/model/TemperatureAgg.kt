package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemperatureAgg(
    val datetime: String?,
    val avg_temp: Float?,
    val min_temp: Float?,
    val max_temp: Float?,
    val open_temp: Float?,
    val close_temp: Float?,
    val msg: String?
) : Parcelable
