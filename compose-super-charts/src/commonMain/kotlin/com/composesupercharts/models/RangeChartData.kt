package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class RangeChartEntry(
    val label: String,
    val start: Float,
    val end: Float,
    val color: Color,
    val tooltipData: List<TooltipBubbleData>? = null
)

data class RangeChartData(
    val entries: List<RangeChartEntry>
)
