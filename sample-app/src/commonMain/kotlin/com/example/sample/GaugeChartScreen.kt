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
import com.composesupercharts.components.organisms.GaugeChart
import com.composesupercharts.models.GaugeChartData
import com.composesupercharts.models.GaugeChartStyleConfig

@Composable
fun GaugeChartScreen(onBack: () -> Unit) {
    var currentValue by remember { mutableStateOf(65f) }
    var needleAnimation by remember { mutableStateOf(true) }

    val styleConfig = GaugeChartStyleConfig(
        arcThickness = 20.dp,
        needleWidth = 6.dp,
        padding = 40.dp
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Gauge Chart Demo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(24.dp).height(240.dp), contentAlignment = Alignment.Center) {
                GaugeChart(
                    data = GaugeChartData(currentValue = currentValue, unit = "PSI", minValue = 0f, maxValue = 100f),
                    config = styleConfig
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Current Value: ${currentValue.toInt()}", style = MaterialTheme.typography.labelLarge)
        Slider(value = currentValue, onValueChange = { currentValue = it }, valueRange = 0f..100f)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = needleAnimation, onCheckedChange = { needleAnimation = it })
            Text("Smooth needle animation", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
