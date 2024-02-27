package com.kaan.pictograph.data.model

import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

data class ChartData(
    val id: String,
    val chartElement: AASeriesElement,
    val predictionsElement: AASeriesElement,
    var isSelected: Boolean
)
