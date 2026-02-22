package com.composesupercharts.models

data class ChartPointData(
    val xLabel: String,
    val yValues: List<Float?>,
    val highlightLabels: List<TooltipBubbleData>
)
