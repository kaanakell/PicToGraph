package com.eae.busbarar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextRecognitionRequest(
    val sensor: String?,
    val ndata: Int?,
    //val date: Int?,
    //val time: Int?
) : Parcelable
