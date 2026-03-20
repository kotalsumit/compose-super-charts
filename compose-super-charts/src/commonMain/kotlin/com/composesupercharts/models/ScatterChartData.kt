package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class ScatterPoint(
    val x: Float,
    val y: Float,
    val label: String? = null,
    val color: Color? = null,
    val radius: Float? = null
)

data class ScatterSeries(
    val points: List<ScatterPoint>,
    val label: String,
    val color: Color
)

data class ScatterChartData(
    val series: List<ScatterSeries>,
    val xTitle: String? = null,
    val yTitle: String? = null
)
