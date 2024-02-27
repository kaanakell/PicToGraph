package com.kaan.pictograph.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenResponse(
    val message: String?,
    val Status: String?,
): Parcelable
