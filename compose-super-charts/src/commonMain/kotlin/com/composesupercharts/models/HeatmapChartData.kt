package com.composesupercharts.models

data class HeatmapCell(
    val row: Int,
    val col: Int,
    val value: Float,
    val label: String? = null
)

data class HeatmapChartData(
    val cells: List<HeatmapCell>,
    val rowLabels: List<String>? = null,
    val columnLabels: List<String>? = null,
    val minValue: Float? = null,
    val maxValue: Float? = null
)
