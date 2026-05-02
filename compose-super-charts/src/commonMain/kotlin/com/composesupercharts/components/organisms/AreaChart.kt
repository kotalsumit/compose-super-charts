package com.composesupercharts.components.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.models.ChartLineConfig
import com.composesupercharts.models.ChartPointData
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.utils.ChartAccessibility.areaChartDescription

@Composable
fun AreaChart(
    modifier: Modifier = Modifier,
    points: List<ChartPointData>,
    maxY: Int,
    yAxisLabel: String?,
    legendLabels: List<String>? = null,
    config: ChartStyleConfig
) {
    val areaConfig = config.copy(
        lines = config.lines.map { line ->
            if (line.fillGradientColors != null) {
                line
            } else {
                ChartLineConfig(
                    lineStyle = line.lineStyle,
                    pointStyle = line.pointStyle,
                    fillGradientColors = listOf(
                        line.lineStyle.color.copy(alpha = 0.28f),
                        Color.Transparent
                    ),
                    isVisible = line.isVisible
                )
            }
        }
    )

    LineChart(
        modifier = modifier.semantics {
            areaChartDescription(
                seriesCount = areaConfig.lines.size,
                points = points,
                yAxisLabel = yAxisLabel
            )
        },
        points = points,
        maxY = maxY,
        yAxisLabel = yAxisLabel,
        legendLabels = legendLabels,
        config = areaConfig
    )
}
