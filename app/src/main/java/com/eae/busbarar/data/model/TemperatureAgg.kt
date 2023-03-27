package com.eae.busbarar.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemperatureAgg(
    @SerializedName("datetime")
    val datetime: String?,
    @SerializedName("avg_temp")
    val avg_temp: Float?,
    @SerializedName("min_temp")
    val min_temp: Float?,
    @SerializedName("max_temp")
    val max_temp: Float?,
    @SerializedName("open_temp")
    val open_temp: Float?,
    @SerializedName("close_temp")
    val close_temp: Float?,
    @SerializedName("msg")
    val msg: String?
):Parcelable
