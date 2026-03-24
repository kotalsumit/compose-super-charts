package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class BarChartPoint(
    val label: String,
    val values: List<Float>,
    val colors: List<Color>,
    val tooltipData: List<TooltipBubbleData>? = null
)

data class BarChartData(
    val points: List<BarChartPoint>
)
