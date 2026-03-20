package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class RadarEntry(
    val value: Float,
    val label: String? = null
)

data class RadarSeries(
    val entries: List<RadarEntry>,
    val label: String,
    val color: Color,
    val fillAlpha: Float = 0.3f
)

data class RadarChartData(
    val series: List<RadarSeries>,
    val axisLabels: List<String>,
    val maxValue: Float? = null
)
