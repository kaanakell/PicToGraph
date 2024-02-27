package com.kaan.pictograph.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
@Parcelize
data class ApiResponse(
    val Status: Boolean?,
    val Message: String?,
    val FileName: String,
):Parcelable
