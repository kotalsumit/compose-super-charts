package com.example.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.organisms.AreaChart
import com.composesupercharts.components.organisms.CombinedChart
import com.composesupercharts.components.organisms.RangeChart
import com.composesupercharts.models.ChartLineConfig
import com.composesupercharts.models.ChartPointData
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.CombinedChartData
import com.composesupercharts.models.CombinedChartPoint
import com.composesupercharts.models.CombinedChartStyleConfig
import com.composesupercharts.models.HollowPoint
import com.composesupercharts.models.LegendPosition
import com.composesupercharts.models.RangeChartData
import com.composesupercharts.models.RangeChartEntry
import com.composesupercharts.models.RangeChartStyleConfig
import com.composesupercharts.models.SolidLine
import com.composesupercharts.models.TooltipBubbleData

@Composable
fun AreaChartScreen(onBack: () -> Unit) {
    var showCloseButton by remember { mutableStateOf(true) }
    var allowLegendToggle by remember { mutableStateOf(true) }
    var xAxisRotation by remember { mutableStateOf(-35f) }

    val points = List(8) { index ->
        val value = 20f + ((index * 13) % 45)
        ChartPointData(
            xLabel = "D${index + 1}",
            yValues = listOf(value),
            highlightLabels = listOf(TooltipBubbleData("Value", value.toInt().toString()))
        )
    }

    ChartDemoShell(
        title = "Area Chart Demo",
        onBack = onBack,
        description = "Filled trend chart for volume-style data."
    ) {
        Column {
            AreaChart(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                points = points,
                maxY = 6,
                yAxisLabel = "Volume",
                legendLabels = listOf("Traffic"),
                config = ChartStyleConfig(
                    lines = listOf(
                        ChartLineConfig(
                            lineStyle = SolidLine(Color(0xFF29B6F6)),
                            pointStyle = HollowPoint(radius = 6f)
                        )
                    ),
                    xAxisLabelRotation = xAxisRotation,
                    showTooltipCloseButton = showCloseButton,
                    allowLegendToggle = allowLegendToggle
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showCloseButton, onCheckedChange = { showCloseButton = it })
                Text("Tooltip close button")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = allowLegendToggle, onCheckedChange = { allowLegendToggle = it })
                Text("Tap legend to hide/show series")
            }
            Text("X-Axis Rotation: ${xAxisRotation.toInt()}°", style = MaterialTheme.typography.labelLarge)
            Slider(value = xAxisRotation, onValueChange = { xAxisRotation = it }, valueRange = -90f..0f)
        }
    }
}

@Composable
fun CombinedChartScreen(onBack: () -> Unit) {
    var legendPosition by remember { mutableStateOf(LegendPosition.TOP) }
    var showValueLabels by remember { mutableStateOf(false) }
    var showCloseButton by remember { mutableStateOf(true) }

    val data = CombinedChartData(
        points = List(7) { index ->
            CombinedChartPoint(
                label = "W${index + 1}",
                columnValue = 25f + ((index * 11) % 50),
                lineValue = 30f + ((index * 7) % 45),
                columnColor = Color(0xFF7E57C2),
                lineColor = Color(0xFFFFA726)
            )
        }
    )

    ChartDemoShell(
        title = "Combined Chart Demo",
        onBack = onBack,
        description = "Columns and line values in one view for comparing two related measures."
    ) {
        Column {
            CombinedChart(
                modifier = Modifier.fillMaxWidth(),
                data = data,
                config = CombinedChartStyleConfig(
                    legendPosition = legendPosition,
                    columnLegendLabel = "Revenue",
                    lineLegendLabel = "Target",
                    showValueLabels = showValueLabels,
                    showTooltipCloseButton = showCloseButton,
                    tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
                    tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold),
                    valueFormatter = { it.toInt().toString() }
                )
            )
            Text("Legend Position", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                LegendPosition.entries.forEach { pos ->
                    FilterChip(selected = legendPosition == pos, onClick = { legendPosition = pos }, label = { Text(pos.name) })
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showValueLabels, onCheckedChange = { showValueLabels = it })
                Text("Value labels")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showCloseButton, onCheckedChange = { showCloseButton = it })
                Text("Tooltip close button")
            }
        }
    }
}

@Composable
fun RangeChartScreen(onBack: () -> Unit) {
    var showCloseButton by remember { mutableStateOf(true) }
    var compactRows by remember { mutableStateOf(false) }

    val data = RangeChartData(
        entries = listOf(
            RangeChartEntry("Design", 2f, 7f, Color(0xFF42A5F5)),
            RangeChartEntry("API", 5f, 11f, Color(0xFF66BB6A)),
            RangeChartEntry("QA", 9f, 14f, Color(0xFFFFA726)),
            RangeChartEntry("Release", 13f, 18f, Color(0xFFEF5350))
        )
    )

    ChartDemoShell(
        title = "Range Chart Demo",
        onBack = onBack,
        description = "Interval chart for schedules, ranges, and start-end values."
    ) {
        Column {
            RangeChart(
                modifier = Modifier.fillMaxWidth(),
                data = data,
                config = RangeChartStyleConfig(
                    rowSpacing = if (compactRows) 8.dp else 18.dp,
                    showTooltipCloseButton = showCloseButton,
                    tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
                    tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold),
                    valueFormatter = { "Day ${it.toInt()}" }
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = showCloseButton, onCheckedChange = { showCloseButton = it })
                Text("Tooltip close button")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = compactRows, onCheckedChange = { compactRows = it })
                Text("Compact rows")
            }
        }
    }
}

@Composable
private fun ChartDemoShell(
    title: String,
    onBack: () -> Unit,
    description: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        ChartScreenHeader(title = title, description = description, onBack = onBack)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                content()
            }
        }
    }
}
