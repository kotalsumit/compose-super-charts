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
import com.composesupercharts.components.organisms.PieChart
import com.composesupercharts.models.PieChartData
import com.composesupercharts.models.PieChartSlice
import com.composesupercharts.models.PieChartStyleConfig

@Composable
fun PieChartScreen(onBack: () -> Unit) {
    var sliceCount by remember { mutableStateOf(5) }
    var innerRadiusRatio by remember { mutableStateOf(0.5f) }
    var sliceSpacing by remember { mutableStateOf(2f) }
    var startAngle by remember { mutableStateOf(-90f) }
    var isSpie by remember { mutableStateOf(false) }
    var isExploded by remember { mutableStateOf(false) }

    val colors = listOf(
        Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC),
        Color(0xFF26A69A), Color(0xFFEC407A), Color(0xFF5C6BC0), Color(0xFFD4E157), Color(0xFF78909C)
    )

    val labels = List(10) { "Category ${it + 1}" }
    
    val dataSlices = List(sliceCount) { idx ->
        PieChartSlice(
            label = labels[idx],
            value = 20f + (idx * 10) % 40f,
            color = colors[idx],
            radiusRatio = if (isSpie) 0.6f + (idx * 0.1f) % 0.4f else 1.0f,
            offsetRatio = if (isExploded) 0.05f else 0f
        )
    }

    val styleConfig = PieChartStyleConfig(
        innerRadiusRatio = innerRadiusRatio,
        sliceSpacing = sliceSpacing,
        startAngle = startAngle,
        chartSize = 280.dp,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pie Chart Demo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                PieChart(
                    data = PieChartData(dataSlices),
                    config = styleConfig
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))

        Text("Slices: $sliceCount", style = MaterialTheme.typography.labelLarge)
        Slider(value = sliceCount.toFloat(), onValueChange = { sliceCount = it.toInt() }, valueRange = 1f..10f)

        Text("Inner Radius: ${(innerRadiusRatio * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
        Slider(value = innerRadiusRatio, onValueChange = { innerRadiusRatio = it }, valueRange = 0f..0.8f)

        Text("Slice Spacing", style = MaterialTheme.typography.labelLarge)
        Slider(value = sliceSpacing, onValueChange = { sliceSpacing = it }, valueRange = 0f..10f)

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = isSpie,
                onClick = { isSpie = !isSpie },
                label = { Text("Spie Mode") },
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = isExploded,
                onClick = { isExploded = !isExploded },
                label = { Text("Exploded") }
            )
        }
    }
}
