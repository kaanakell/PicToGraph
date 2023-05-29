package com.eae.busbarar.data.model

import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

data class ChartData(
    val sensorId: String,
    val chartElement: AASeriesElement,
    var isSelected: Boolean
)
