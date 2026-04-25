package com.example.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.organisms.BarChart
import com.composesupercharts.models.*

@Composable
fun BarChartScreen(onBack: () -> Unit) {
    var chartType by remember { mutableStateOf(BarChartType.STACKED) }
    var pointCount by remember { mutableStateOf(5) }
    var barCountPerPoint by remember { mutableStateOf(3) }
    var legendPosition by remember { mutableStateOf(LegendPosition.BOTTOM) }
    var showValueLabels by remember { mutableStateOf(false) }
    var allowLegendToggle by remember { mutableStateOf(true) }

    val colors = listOf(
        Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC)
    )

    val labels = List(10) { "Task ${it + 1}" }
    val dataPoints = List(pointCount) { ptIdx ->
        BarChartPoint(
            label = labels[ptIdx],
            values = List(barCountPerPoint) { valIdx -> 20f + (ptIdx * 10f + valIdx * 5f) % 50f },
            colors = colors.take(barCountPerPoint)
        )
    }

    val styleConfig = BarChartStyleConfig(
        type = chartType,
        legendPosition = legendPosition,
        barThickness = 16.dp,
        barSpacing = 8.dp,
        showValueLabels = showValueLabels,
        allowLegendToggle = allowLegendToggle,
        valueFormatter = { it.toInt().toString() },
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        ChartScreenHeader(
            title = "Bar Chart Demo",
            description = "Horizontal bars for comparing categories with standard, clustered, and stacked layouts.",
            onBack = onBack
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                BarChart(
                    data = BarChartData(dataPoints),
                    maxX = 5,
                    legendLabels = if (chartType != BarChartType.STANDARD) List(barCountPerPoint) { "Group ${it + 1}" } else null,
                    config = styleConfig
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Chart Type", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            BarChartType.entries.forEach { type ->
                FilterChip(
                    selected = chartType == type,
                    onClick = { chartType = type },
                    label = { Text(type.name) }
                )
            }
        }

        Text("Points: $pointCount", style = MaterialTheme.typography.labelLarge)
        Slider(value = pointCount.toFloat(), onValueChange = { pointCount = it.toInt() }, valueRange = 2f..10f)

        if (chartType != BarChartType.STANDARD) {
            Text("Bars per Point: $barCountPerPoint", style = MaterialTheme.typography.labelLarge)
            Slider(value = barCountPerPoint.toFloat(), onValueChange = { barCountPerPoint = it.toInt() }, valueRange = 1f..5f)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = showValueLabels, onCheckedChange = { showValueLabels = it })
            Text("Value labels")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = allowLegendToggle, onCheckedChange = { allowLegendToggle = it })
            Text("Tap legend to hide/show series")
        }
    }
}
