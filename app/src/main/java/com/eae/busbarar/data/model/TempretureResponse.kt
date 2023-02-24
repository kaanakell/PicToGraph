package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemperatureResponse(
    val temps: List<Temperature>?
) : Parcelable