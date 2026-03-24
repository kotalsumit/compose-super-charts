package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class PieChartSlice(
    val label: String,
    val value: Float,
    val color: Color,
    val radiusRatio: Float = 1.0f,
    val offsetRatio: Float = 0f,
    val tooltipData: List<TooltipBubbleData>? = null
)

data class PieChartData(
    val slices: List<PieChartSlice>
)
