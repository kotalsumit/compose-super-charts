package com.example.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composesupercharts.components.organisms.*
import com.composesupercharts.models.*

@Composable
fun App() {
    var isDarkTheme by remember { mutableStateOf<Boolean?>(null) }
    val useDarkTheme = isDarkTheme ?: isSystemInDarkTheme()

    val colorScheme = if (useDarkTheme) {
        darkColorScheme(
            primary = Color(0xFF90CAF9),
            secondary = Color(0xFFBB86FC),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF7B1FA2),
            background = Color(0xFFF5F5F5),
            surface = Color.White
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var currentScreen by remember { mutableStateOf("HOME") }

            when (currentScreen) {
                "HOME" -> HomeScreen(
                    isDark = useDarkTheme,
                    onToggleTheme = { isDarkTheme = !useDarkTheme },
                    onNavigate = { currentScreen = it }
                )
                "LINE_CHART" -> LineChartScreen(onBack = { currentScreen = "HOME" })
                "PIE_CHART" -> PieChartScreen(onBack = { currentScreen = "HOME" })
                "COLUMN_CHART" -> ColumnChartScreen(onBack = { currentScreen = "HOME" })
                "BAR_CHART" -> BarChartScreen(onBack = { currentScreen = "HOME" })
                "PYRAMID_CHART" -> PyramidChartScreen(onBack = { currentScreen = "HOME" })
                "BUBBLE_CHART" -> BubbleChartScreen(onBack = { currentScreen = "HOME" })
                "VENN_DIAGRAM" -> VennDiagramScreen(onBack = { currentScreen = "HOME" })
                "GAUGE_CHART" -> GaugeChartScreen(onBack = { currentScreen = "HOME" })
                "SCATTER_CHART" -> ScatterChartScreen(onBack = { currentScreen = "HOME" })
                "HEATMAP_CHART" -> HeatmapChartScreen(onBack = { currentScreen = "HOME" })
                "RADAR_CHART" -> RadarChartScreen(onBack = { currentScreen = "HOME" })
                "CANDLESTICK_CHART" -> CandlestickChartScreen(onBack = { currentScreen = "HOME" })
            }
        }
    }
}

data class ChartTypeItem(
    val title: String,
    val id: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val chartGalleryItems = listOf(
        ChartTypeItem("Line Chart", "LINE_CHART", Icons.Default.ShowChart, Color(0xFF42A5F5)),
        ChartTypeItem("Pie Chart", "PIE_CHART", Icons.Default.PieChart, Color(0xFF66BB6A)),
        ChartTypeItem("Column Chart", "COLUMN_CHART", Icons.Default.BarChart, Color(0xFFFFA726)),
        ChartTypeItem("Bar Chart", "BAR_CHART", Icons.Default.AlignHorizontalLeft, Color(0xFFEF5350)),
        ChartTypeItem("Pyramid Chart", "PYRAMID_CHART", Icons.Default.FilterFrames, Color(0xFFAB47BC)),
        ChartTypeItem("Bubble Chart", "BUBBLE_CHART", Icons.Default.BubbleChart, Color(0xFF26A69A)),
        ChartTypeItem("Venn Diagram", "VENN_DIAGRAM", Icons.Default.DonutSmall, Color(0xFFFF7043)),
        ChartTypeItem("Gauge Chart", "GAUGE_CHART", Icons.Default.Speed, Color(0xFF5C6BC0)),
        ChartTypeItem("Scatter Chart", "SCATTER_CHART", Icons.Default.ScatterPlot, Color(0xFFFFCA28)),
        ChartTypeItem("Heatmap", "HEATMAP_CHART", Icons.Default.GridOn, Color(0xFF78909C)),
        ChartTypeItem("Radar Chart", "RADAR_CHART", Icons.Default.DashboardCustomize, Color(0xFF8D6E63)),
        ChartTypeItem("Candlestick", "CANDLESTICK_CHART", Icons.Default.Equalizer, Color(0xFFEC407A))
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = if (isDark) listOf(Color(0xFF37474F), Color(0xFF263238))
                        else listOf(Color(0xFF1976D2), Color(0xFF1565C0))
                    )
                )
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Super Charts",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        "Beautiful Compose Multiplatform Charts",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = Color.White
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(chartGalleryItems) { chart ->
                ChartCard(chart = chart, onClick = { onNavigate(chart.id) })
            }
        }
    }
}

@Composable
fun ChartCard(chart: ChartTypeItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(chart.color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = chart.icon,
                    contentDescription = null,
                    tint = chart.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = chart.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun LineChartScreen(onBack: () -> Unit) {
    var lineCount by remember { mutableStateOf(1) }
    var isScrollable by remember { mutableStateOf(false) }
    var gradientProductIndex by remember { mutableStateOf(0) }
    var legendPos by remember { mutableStateOf(LegendPosition.TOP) }
    var xAxisRotation by remember { mutableStateOf(-45f) }
    var yAxisColor by remember { mutableStateOf(Color.LightGray) }
    var showTooltipClose by remember { mutableStateOf(true) }
    var autoDismiss by remember { mutableStateOf<Long?>(null) }

    val colors = listOf(Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC))
    val lineNames = listOf("Product A", "Product B", "Product C", "Product D", "Product E")

    val basePoints = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Week 2", "Week 3", "Week 4")
    val displayPoints = if (isScrollable) basePoints else basePoints.take(7)

    val points = displayPoints.mapIndexed { index, day ->
        val yVals = List(lineCount) { lineIdx ->
            (20f + (lineIdx * 10f) + (index * 5f) % 25f)
        }
        val tooltipLabels = List(lineCount) { lineIdx ->
            TooltipBubbleData(lineNames[lineIdx], yVals[lineIdx].toInt().toString())
        }
        ChartPointData(xLabel = day, yValues = yVals, highlightLabels = tooltipLabels)
    }

    val configLines = List(lineCount) { idx ->
        val lineStyle = if (idx % 2 == 1) DashedLine(color = colors[idx], width = 3f) else SolidLine(color = colors[idx], width = 4f)
        val pointStyle = if (idx % 2 == 1) HollowPoint(radius = 6f) else SolidPoint(radius = 6f)
        ChartLineConfig(
            lineStyle = lineStyle,
            pointStyle = pointStyle,
            fillGradientColors = if (idx == gradientProductIndex) listOf(colors[idx].copy(alpha = 0.3f), Color.Transparent) else null
        )
    }

    val styleConfig = ChartStyleConfig(
        lines = configLines,
        isScrollable = isScrollable,
        isClickable = true,
        legendPosition = legendPos,
        xAxisLabelRotation = xAxisRotation,
        yAxisDividerColor = yAxisColor,
        xAxisDividerColor = yAxisColor,
        showTooltipCloseButton = showTooltipClose,
        tooltipAutoDismissMs = autoDismiss
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Line Chart Demo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Text("Series Count", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            (1..5).forEach { count ->
                FilterChip(
                    selected = lineCount == count,
                    onClick = { lineCount = count },
                    label = { Text("$count") }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Checkbox(checked = isScrollable, onCheckedChange = { isScrollable = it })
            Text("Enable Horizontal Scrolling")
        }

        Text("Legend Position", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LegendPosition.entries.forEach { pos ->
                FilterChip(
                    selected = legendPos == pos,
                    onClick = { legendPos = pos },
                    label = { Text(pos.name) }
                )
            }
        }

        Text("Tooltip Controls", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = showTooltipClose, onCheckedChange = { showTooltipClose = it })
            Text("Show Close Button")
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
            listOf(null, 2000L, 5000L).forEach { ms ->
                FilterChip(
                    selected = autoDismiss == ms,
                    onClick = { autoDismiss = ms },
                    label = { Text(if (ms == null) "No Auto-Close" else "${ms / 1000}s") }
                )
            }
        }

        Text("X-Axis Label Rotation", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp))
        Slider(
            value = xAxisRotation,
            onValueChange = { xAxisRotation = it },
            valueRange = -90f..90f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                LineChart(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    points = points,
                    maxY = 5,
                    yAxisLabel = "Sales Quantity",
                    legendLabels = lineNames.take(lineCount),
                    config = styleConfig
                )
            }
        }
    }
}
