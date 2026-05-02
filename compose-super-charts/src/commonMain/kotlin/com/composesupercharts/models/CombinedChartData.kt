package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class CombinedChartPoint(
    val label: String,
    val columnValue: Float,
    val lineValue: Float,
    val columnColor: Color,
    val lineColor: Color
)

data class CombinedChartData(
    val points: List<CombinedChartPoint>
)
