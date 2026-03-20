package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class PyramidChartSegment(
    val label: String,
    val value: Float,
    val color: Color,
    val tooltipData: List<TooltipBubbleData>? = null
)

data class PyramidChartData(
    val segments: List<PyramidChartSegment>
)
