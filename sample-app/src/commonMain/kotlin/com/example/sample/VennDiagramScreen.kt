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
import com.composesupercharts.components.organisms.VennDiagram
import com.composesupercharts.models.*

@Composable
fun VennDiagramScreen(onBack: () -> Unit) {
    var opacity by remember { mutableStateOf(0.5f) }
    
    val sets = listOf(
        VennSet(id = "logic", label = "Logic", value = 100f, color = Color(0xFF42A5F5)),
        VennSet(id = "creativity", label = "Creativity", value = 100f, color = Color(0xFFEF5350))
    )
    
    val intersections = listOf(
        VennIntersection(setIds = setOf("logic", "creativity"), label = "Innovation", value = 30f)
    )

    val data = VennDiagramData(sets = sets, intersections = intersections)

    val styleConfig = VennDiagramStyleConfig(
        circleOpacity = opacity,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        ChartScreenHeader(
            title = "Venn Diagram Demo",
            description = "Shows overlap between sets while keeping intersections available for tooltips.",
            onBack = onBack
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp).height(300.dp), contentAlignment = Alignment.Center) {
                VennDiagram(data = data, config = styleConfig)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Circle Opacity: ${(opacity * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
        Slider(value = opacity, onValueChange = { opacity = it }, valueRange = 0.1f..1f)
    }
}
