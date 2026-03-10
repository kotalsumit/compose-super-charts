package com.composesupercharts.utils

import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import com.composesupercharts.models.ChartPointData
import com.composesupercharts.models.PieChartData

/**
 * Utility to provide meaningful content descriptions for charts.
 */
object ChartAccessibility {

    fun SemanticsPropertyReceiver.lineChartDescription(
        seriesCount: Int,
        points: List<ChartPointData>,
        yAxisLabel: String?
    ) {
        val label = yAxisLabel ?: "data"
        val description = buildString {
            append("Line Chart showing $seriesCount series of $label. ")
            append("Data contains ${points.size} points on the X-axis. ")
            if (points.isNotEmpty()) {
                append("First point at ${points.first().xLabel}. ")
                append("Last point at ${points.last().xLabel}. ")
            }
        }
        contentDescription = description
    }

    fun SemanticsPropertyReceiver.pieChartDescription(
        data: PieChartData
    ) {
        val description = buildString {
            append("Pie Chart showing ${data.slices.size} slices. ")
            data.slices.forEach { slice ->
                append("${slice.label ?: "Slice"}: ${slice.value}. ")
            }
        }
        contentDescription = description
    }

    fun SemanticsPropertyReceiver.barChartDescription(
        seriesCount: Int,
        points: List<ChartPointData>,
        yAxisLabel: String?
    ) {
        val label = yAxisLabel ?: "data"
        contentDescription = "Bar Chart showing $seriesCount series of $label with ${points.size} categories."
    }
}
