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
import com.composesupercharts.components.organisms.HeatmapChart
import com.composesupercharts.models.HeatmapCell
import com.composesupercharts.models.HeatmapChartData
import com.composesupercharts.models.HeatmapChartStyleConfig
import kotlin.random.Random

@Composable
fun HeatmapChartScreen(onBack: () -> Unit) {
    var rows by remember { mutableStateOf(10) }
    var cols by remember { mutableStateOf(10) }
    var spacing by remember { mutableStateOf(4.dp) }

    val random = remember { Random(55) }
    val cells = remember(rows, cols) {
        List(rows * cols) { i ->
            HeatmapCell(i / cols, i % cols, random.nextFloat() * 100f)
        }
    }

    val styleConfig = HeatmapChartStyleConfig(
        cellSpacing = spacing,
        cornerRadius = 4.dp,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        ChartScreenHeader(
            title = "Heatmap Demo",
            description = "Color intensity grid for spotting density, frequency, and category patterns.",
            onBack = onBack
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp).height(320.dp), contentAlignment = Alignment.Center) {
                HeatmapChart(
                    data = HeatmapChartData(cells),
                    config = styleConfig,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Grid Size: ${rows}x${cols}", style = MaterialTheme.typography.labelLarge)
        Slider(value = rows.toFloat(), onValueChange = { rows = it.toInt(); cols = it.toInt() }, valueRange = 5f..20f)

        Text("Spacing: ${spacing.value.toInt()}dp", style = MaterialTheme.typography.labelLarge)
        Slider(value = spacing.value, onValueChange = { spacing = it.dp }, valueRange = 0f..12f)
    }
}
