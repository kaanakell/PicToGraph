package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileNameResponse(
    val Status: String?,
    val DetectedSensor: String?
) : Parcelable
