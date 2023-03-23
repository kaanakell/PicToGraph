package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextRecognitionRequest(
    val sensor: String?,
    val aggvalue: Int,
    val startdt: String?,
    val enddt: String?,
    val aggunit: String = "m"
) : Parcelable
