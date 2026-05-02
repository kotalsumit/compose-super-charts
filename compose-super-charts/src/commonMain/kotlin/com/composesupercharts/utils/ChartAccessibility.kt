package com.composesupercharts.utils

import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import com.composesupercharts.models.ChartPointData
import com.composesupercharts.models.PieChartData
import com.composesupercharts.models.RadarChartData
import com.composesupercharts.models.VennDiagramData
import com.composesupercharts.models.GaugeChartData
import com.composesupercharts.models.HeatmapChartData
import com.composesupercharts.models.ScatterChartData
import com.composesupercharts.models.BubbleChartData
import com.composesupercharts.models.ColumnChartData
import com.composesupercharts.models.CandlestickChartData
import com.composesupercharts.models.CombinedChartData
import com.composesupercharts.models.RangeChartData

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
                append("${slice.label}: ${slice.value}. ")
            }
        }
        contentDescription = description
    }

    fun SemanticsPropertyReceiver.barChartDescription(
        seriesCount: Int,
        pointsCount: Int,
        yAxisLabel: String?
    ) {
        val label = yAxisLabel ?: "data"
        contentDescription = "Bar Chart showing $seriesCount series of $label with $pointsCount categories."
    }

    fun SemanticsPropertyReceiver.radarChartDescription(data: RadarChartData) {
        contentDescription = "Radar Chart with ${data.axisLabels.size} axes and ${data.series.size} series."
    }

    fun SemanticsPropertyReceiver.vennDiagramDescription(data: VennDiagramData) {
        contentDescription = "Venn Diagram showing ${data.sets.size} overlapping sets."
    }

    fun SemanticsPropertyReceiver.gaugeChartDescription(data: GaugeChartData) {
        contentDescription = "Gauge Chart showing value ${data.currentValue} in range ${data.minValue} to ${data.maxValue}."
    }

    fun SemanticsPropertyReceiver.heatmapDescription(data: HeatmapChartData) {
        contentDescription = "Heatmap Chart with ${data.cells.size} data points."
    }

    fun SemanticsPropertyReceiver.scatterChartDescription(data: ScatterChartData) {
        val totalPoints = data.series.sumOf { it.points.size }
        contentDescription = "Scatter Chart showing $totalPoints points across ${data.series.size} series."
    }

    fun SemanticsPropertyReceiver.bubbleChartDescription(data: BubbleChartData) {
        contentDescription = "Bubble Chart showing ${data.points.size} bubbles."
    }

    fun SemanticsPropertyReceiver.columnChartDescription(seriesCount: Int, categoriesCount: Int) {
        contentDescription = "Column Chart showing $seriesCount series with $categoriesCount categories."
    }

    fun SemanticsPropertyReceiver.candlestickChartDescription(data: CandlestickChartData) {
        contentDescription = "Candlestick Chart showing ${data.entries.size} financial data points."
    }

    fun SemanticsPropertyReceiver.areaChartDescription(
        seriesCount: Int,
        points: List<ChartPointData>,
        yAxisLabel: String?
    ) {
        val label = yAxisLabel ?: "data"
        contentDescription = "Area Chart showing $seriesCount series of $label with ${points.size} points."
    }

    fun SemanticsPropertyReceiver.combinedChartDescription(data: CombinedChartData) {
        contentDescription = "Combined Chart showing columns and a line across ${data.points.size} categories."
    }

    fun SemanticsPropertyReceiver.rangeChartDescription(data: RangeChartData) {
        contentDescription = "Range Chart showing ${data.entries.size} start and end intervals."
    }
}
