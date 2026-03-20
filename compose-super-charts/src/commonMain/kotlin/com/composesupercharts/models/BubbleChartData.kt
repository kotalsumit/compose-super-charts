package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class BubbleChartPoint(
    val x: Float,
    val y: Float,
    val size: Float,
    val label: String,
    val color: Color,
    val tooltipData: List<TooltipBubbleData>? = null
)

data class BubbleChartData(
    val points: List<BubbleChartPoint>
)
