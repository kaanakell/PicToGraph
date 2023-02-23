package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextRecognitionRequest(
    val sensor: String?,
    val ndata: Int?,
    val startdt: String?,
    val enddt: String?
    //val avg: Int?
) : Parcelable
