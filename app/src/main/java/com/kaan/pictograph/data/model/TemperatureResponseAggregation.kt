package com.kaan.pictograph.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class TemperatureResponseAggregation(
    @SerializedName("datetime")
    val datetime: Int?,
    @SerializedName("pred")
    val pred: Int?,
    @SerializedName("sensor")
    val sensor: Int?,
    @SerializedName("average")
    val average: Float?,
    @SerializedName("min")
    val min: Float?,
    @SerializedName("max")
    val max: Float?,
    @SerializedName("open")
    val open: Float?,
    @SerializedName("close")
    val close: Float?
) : Parcelable




