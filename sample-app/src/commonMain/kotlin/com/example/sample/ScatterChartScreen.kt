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
import com.composesupercharts.components.organisms.ScatterChart
import com.composesupercharts.models.*
import kotlin.random.Random

@Composable
fun ScatterChartScreen(onBack: () -> Unit) {
    val random = remember { Random(99) }
    var showGridLines by remember { mutableStateOf(true) }
    
    val series = remember {
        listOf(
            ScatterSeries(
                points = List(20) { i ->
                    ScatterPoint(
                        x = random.nextFloat() * 100f,
                        y = random.nextFloat() * 100f,
                        label = "A_$i",
                        radius = 6f + random.nextFloat() * 8f
                    )
                },
                label = "Product A",
                color = Color(0xFF42A5F5)
            ),
            ScatterSeries(
                points = List(20) { i ->
                    ScatterPoint(
                        x = random.nextFloat() * 100f,
                        y = random.nextFloat() * 100f,
                        label = "B_$i",
                        radius = 6f + random.nextFloat() * 8f
                    )
                },
                label = "Product B",
                color = Color(0xFFEF5350)
            )
        )
    }

    val styleConfig = ScatterChartStyleConfig(
        showGridLines = showGridLines,
        padding = 40.dp,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scatter Chart Demo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp).height(350.dp)) {
                ScatterChart(
                    data = ScatterChartData(series = series),
                    config = styleConfig,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = showGridLines, onCheckedChange = { showGridLines = it })
            Text("Show Grid Lines")
        }
    }
}
