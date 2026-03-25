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
import com.composesupercharts.components.organisms.RadarChart
import com.composesupercharts.models.*
import kotlin.random.Random

@Composable
fun RadarChartScreen(onBack: () -> Unit) {
    var seriesCount by remember { mutableStateOf(2) }
    var webType by remember { mutableStateOf(RadarWebType.POLYGON) }
    var levels by remember { mutableStateOf(5) }

    val random = remember { Random(42) }
    val axisLabels = listOf("Strength", "Agility", "Intelligence", "Stamina", "Luck", "Defense")
    
    val series = remember(seriesCount) {
        List(seriesCount) { sIdx ->
            RadarSeries(
                entries = axisLabels.map { label ->
                    RadarEntry(40f + random.nextFloat() * 60f, label)
                },
                label = if (sIdx == 0) "Hero A" else "Hero B",
                color = if (sIdx == 0) Color(0xFF42A5F5) else Color(0xFFEF5350)
            )
        }
    }

    val styleConfig = RadarChartStyleConfig(
        webType = webType,
        levels = levels,
        padding = 40.dp,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Radar Chart Demo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp).height(320.dp), contentAlignment = Alignment.Center) {
                RadarChart(
                    data = RadarChartData(series, axisLabels, 100f),
                    config = styleConfig
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Web Style", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            RadarWebType.entries.forEach { type ->
                FilterChip(
                    selected = webType == type,
                    onClick = { webType = type },
                    label = { Text(type.name) }
                )
            }
        }

        Text("Series: $seriesCount", style = MaterialTheme.typography.labelLarge)
        Slider(value = seriesCount.toFloat(), onValueChange = { seriesCount = it.toInt() }, valueRange = 1f..3f)

        Text("Web Levels: $levels", style = MaterialTheme.typography.labelLarge)
        Slider(value = levels.toFloat(), onValueChange = { levels = it.toInt() }, valueRange = 3f..8f)
    }
}
