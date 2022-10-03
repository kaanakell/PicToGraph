package com.example.cameraeae.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val name: String,
    val path: String,
) : Parcelable
