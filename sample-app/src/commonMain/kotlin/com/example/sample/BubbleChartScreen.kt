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
import com.composesupercharts.components.organisms.BubbleChart
import com.composesupercharts.models.*
import kotlin.random.Random

@Composable
fun BubbleChartScreen(onBack: () -> Unit) {
    val random = remember { Random(77) }
    var opacity by remember { mutableStateOf(0.6f) }
    
    val points = remember {
        List(15) { i ->
            BubbleChartPoint(
                x = random.nextFloat() * 100f,
                y = random.nextFloat() * 100f,
                size = 10f + random.nextFloat() * 20f,
                color = Color(random.nextInt()).copy(alpha = 0.6f),
                label = "Bubble $i"
            )
        }
    }

    val styleConfig = BubbleChartStyleConfig(
        bubbleOpacity = opacity,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        ChartScreenHeader(
            title = "Bubble Chart Demo",
            description = "Plots x/y values with bubble size as a third dimension for magnitude.",
            onBack = onBack
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp).height(400.dp)) {
                BubbleChart(
                    data = BubbleChartData(points),
                    config = styleConfig,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Bubble Opacity: ${(opacity * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
        Slider(value = opacity, onValueChange = { opacity = it }, valueRange = 0.1f..1f)
    }
}
