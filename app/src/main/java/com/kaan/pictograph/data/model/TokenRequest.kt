package com.kaan.pictograph.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenRequest(
    val token: String?,
    val mac: String?
): Parcelable