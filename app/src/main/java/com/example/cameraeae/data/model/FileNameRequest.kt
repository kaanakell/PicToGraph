package com.example.cameraeae.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileNameRequest(
    val filename: String?
) : Parcelable
