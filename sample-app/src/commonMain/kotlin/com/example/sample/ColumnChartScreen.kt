package com.example.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.organisms.ColumnChart
import com.composesupercharts.models.*

@Composable
fun ColumnChartScreen(onBack: () -> Unit) {
    var chartType by remember { mutableStateOf(ColumnChartType.CLUSTERED) }
    var pointCount by remember { mutableStateOf(6) }
    var barCountPerPoint by remember { mutableStateOf(3) }
    var isScrollable by remember { mutableStateOf(false) }
    var legendPosition by remember { mutableStateOf(LegendPosition.TOP) }

    val colors = listOf(
        Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC)
    )

    val labels = List(15) { "Week ${it + 1}" }
    val dataPoints = List(pointCount) { ptIdx ->
        ColumnChartPoint(
            label = labels[ptIdx],
            values = List(barCountPerPoint) { valIdx -> 20f + (ptIdx * 10f + valIdx * 5f) % 50f },
            colors = colors.take(barCountPerPoint)
        )
    }

    val styleConfig = ColumnChartStyleConfig(
        type = chartType,
        isScrollable = isScrollable,
        legendPosition = legendPosition,
        barWidth = 20.dp,
        barSpacing = 8.dp,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Column Chart Demo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                ColumnChart(
                    data = ColumnChartData(dataPoints),
                    maxY = 5,
                    legendLabels = if (chartType != ColumnChartType.STANDARD) List(barCountPerPoint) { "Product ${it + 1}" } else null,
                    config = styleConfig
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Type", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            ColumnChartType.entries.forEach { type ->
                FilterChip(
                    selected = chartType == type,
                    onClick = { chartType = type },
                    label = { Text(type.name) }
                )
            }
        }

        Text("Points: $pointCount", style = MaterialTheme.typography.labelLarge)
        Slider(value = pointCount.toFloat(), onValueChange = { pointCount = it.toInt() }, valueRange = 3f..12f)

        if (chartType != ColumnChartType.STANDARD) {
            Text("Bars per Point: $barCountPerPoint", style = MaterialTheme.typography.labelLarge)
            Slider(value = barCountPerPoint.toFloat(), onValueChange = { barCountPerPoint = it.toInt() }, valueRange = 1f..5f)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isScrollable || chartType == ColumnChartType.CLUSTERED, onCheckedChange = { isScrollable = it }, enabled = chartType != ColumnChartType.CLUSTERED)
            Text("Scrollable")
        }
    }
}
