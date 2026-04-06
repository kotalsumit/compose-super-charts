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
import com.composesupercharts.components.organisms.PyramidChart
import com.composesupercharts.models.*

@Composable
fun PyramidChartScreen(onBack: () -> Unit) {
    var segmentCount by remember { mutableStateOf(5) }
    var isInverted by remember { mutableStateOf(false) }

    val colors = listOf(
        Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC)
    )

    val segments = List(segmentCount) { idx ->
        PyramidChartSegment(
            label = "Step ${idx + 1}",
            value = 100f - (idx * 10f),
            color = colors[idx % colors.size]
        )
    }

    val styleConfig = PyramidChartStyleConfig(
        type = if (isInverted) PyramidChartType.FUNNEL else PyramidChartType.PYRAMID,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isInverted) "Funnel Demo" else "Pyramid Demo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp).height(350.dp), contentAlignment = Alignment.Center) {
                PyramidChart(
                    data = PyramidChartData(segments = segments),
                    config = styleConfig
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Segments: $segmentCount", style = MaterialTheme.typography.labelLarge)
        Slider(value = segmentCount.toFloat(), onValueChange = { segmentCount = it.toInt() }, valueRange = 2f..8f)

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Switch(checked = isInverted, onCheckedChange = { isInverted = it })
            Text(if (isInverted) "Funnel Mode (Inverted)" else "Pyramid Mode", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
