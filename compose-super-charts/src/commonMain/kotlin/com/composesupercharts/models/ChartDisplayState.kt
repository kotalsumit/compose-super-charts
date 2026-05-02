package com.composesupercharts.models

sealed interface ChartDisplayState {
    data object Ready : ChartDisplayState
    data class Empty(val message: String = "No chart data available") : ChartDisplayState
    data class Loading(val message: String = "Loading chart data") : ChartDisplayState
    data class Error(val message: String = "Unable to load chart data") : ChartDisplayState
}
