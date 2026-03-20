package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class GaugeRange(
    val min: Float,
    val max: Float,
    val color: Color,
    val label: String? = null
)

data class GaugeChartData(
    val currentValue: Float,
    val minValue: Float = 0f,
    val maxValue: Float = 100f,
    val ranges: List<GaugeRange> = emptyList(),
    val unit: String? = null
)
