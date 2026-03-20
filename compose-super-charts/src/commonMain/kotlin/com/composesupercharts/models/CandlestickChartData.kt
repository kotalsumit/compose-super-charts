package com.composesupercharts.models

data class CandleEntry(
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val label: String
)

data class CandlestickChartData(
    val entries: List<CandleEntry>,
    val yAxisTitle: String? = null,
    val xAxisTitle: String? = null
)
