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
import androidx.compose.material.icons.automirrored.filled.AlignHorizontalLeft
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composesupercharts.components.organisms.*
import com.composesupercharts.models.*
import com.composesupercharts.utils.formatOneDecimal

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
            val navigateHome = { currentScreen = "HOME" }

            PlatformBackHandler(
                enabled = currentScreen != "HOME",
                onBack = navigateHome
            )

            when (currentScreen) {
                "HOME" -> HomeScreen(
                    isDark = useDarkTheme,
                    onToggleTheme = { isDarkTheme = !useDarkTheme },
                    onNavigate = { currentScreen = it }
                )
                "LINE_CHART" -> LineChartScreen(onBack = navigateHome)
                "PIE_CHART" -> PieChartScreen(onBack = navigateHome)
                "COLUMN_CHART" -> ColumnChartScreen(onBack = navigateHome)
                "BAR_CHART" -> BarChartScreen(onBack = navigateHome)
                "PYRAMID_CHART" -> PyramidChartScreen(onBack = navigateHome)
                "BUBBLE_CHART" -> BubbleChartScreen(onBack = navigateHome)
                "VENN_DIAGRAM" -> VennDiagramScreen(onBack = navigateHome)
                "GAUGE_CHART" -> GaugeChartScreen(onBack = navigateHome)
                "SCATTER_CHART" -> ScatterChartScreen(onBack = navigateHome)
                "HEATMAP_CHART" -> HeatmapChartScreen(onBack = navigateHome)
                "RADAR_CHART" -> RadarChartScreen(onBack = navigateHome)
                "CANDLESTICK_CHART" -> CandlestickChartScreen(onBack = navigateHome)
                "AREA_CHART" -> AreaChartScreen(onBack = navigateHome)
                "COMBINED_CHART" -> CombinedChartScreen(onBack = navigateHome)
                "RANGE_CHART" -> RangeChartScreen(onBack = navigateHome)
            }
        }
    }
}

data class ChartTypeItem(
    val title: String,
    val id: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

@Composable
fun HomeScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val chartGalleryItems = listOf(
        ChartTypeItem("Line Chart", "LINE_CHART", Icons.AutoMirrored.Filled.ShowChart, Color(0xFF42A5F5), "Trends over time"),
        ChartTypeItem("Pie Chart", "PIE_CHART", Icons.Default.PieChart, Color(0xFF66BB6A), "Part-to-whole split"),
        ChartTypeItem("Column Chart", "COLUMN_CHART", Icons.Default.BarChart, Color(0xFFFFA726), "Vertical comparison"),
        ChartTypeItem("Bar Chart", "BAR_CHART", Icons.AutoMirrored.Filled.AlignHorizontalLeft, Color(0xFFEF5350), "Horizontal ranking"),
        ChartTypeItem("Pyramid Chart", "PYRAMID_CHART", Icons.Default.FilterFrames, Color(0xFFAB47BC), "Funnels and stages"),
        ChartTypeItem("Bubble Chart", "BUBBLE_CHART", Icons.Default.BubbleChart, Color(0xFF26A69A), "Three-value points"),
        ChartTypeItem("Venn Diagram", "VENN_DIAGRAM", Icons.Default.DonutSmall, Color(0xFFFF7043), "Set overlap"),
        ChartTypeItem("Gauge Chart", "GAUGE_CHART", Icons.Default.Speed, Color(0xFF5C6BC0), "Single KPI"),
        ChartTypeItem("Scatter Chart", "SCATTER_CHART", Icons.Default.ScatterPlot, Color(0xFFFFCA28), "Correlation map"),
        ChartTypeItem("Heatmap", "HEATMAP_CHART", Icons.Default.GridOn, Color(0xFF78909C), "Intensity grid"),
        ChartTypeItem("Radar Chart", "RADAR_CHART", Icons.Default.DashboardCustomize, Color(0xFF8D6E63), "Multi-axis profile"),
        ChartTypeItem("Candlestick", "CANDLESTICK_CHART", Icons.Default.Equalizer, Color(0xFFEC407A), "OHLC movement"),
        ChartTypeItem("Area Chart", "AREA_CHART", Icons.Default.AreaChart, Color(0xFF29B6F6), "Filled trend"),
        ChartTypeItem("Combined Chart", "COMBINED_CHART", Icons.Default.StackedLineChart, Color(0xFF7E57C2), "Column plus line"),
        ChartTypeItem("Range Chart", "RANGE_CHART", Icons.Default.DateRange, Color(0xFF26A69A), "Intervals")
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
                .padding(vertical = 28.dp, horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        "Compose Super Charts",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Interactive chart components for Android, iOS, and desktop",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.82f)),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
            .height(132.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(chart.color.copy(alpha = 0.14f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = chart.icon,
                    contentDescription = null,
                    tint = chart.color,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column {
                Text(
                    text = chart.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
                Text(
                    text = chart.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ChartScreenHeader(
    title: String,
    description: String,
    onBack: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(24.dp))
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
    var showNullGaps by remember { mutableStateOf(false) }
    var compactAxis by remember { mutableStateOf(false) }

    val colors = listOf(Color(0xFF42A5F5), Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC))
    val lineNames = listOf("Product A", "Product B", "Product C", "Product D", "Product E")

    val basePoints = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Week 2", "Week 3", "Week 4")
    val displayPoints = if (isScrollable) basePoints else basePoints.take(7)

    val points = displayPoints.mapIndexed { index, day ->
        val yVals: List<Float?> = List(lineCount) { lineIdx ->
            val value = 20f + (lineIdx * 10f) + (index * 5f) % 25f
            if (showNullGaps && lineIdx == 0 && index in 2..3) null else value
        }
        val tooltipLabels = List(lineCount) { lineIdx ->
            TooltipBubbleData(lineNames[lineIdx], yVals[lineIdx]?.toInt()?.toString())
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
        tooltipAutoDismissMs = autoDismiss,
        nullPointBehavior = NullPointBehavior.BreakSegment,
        yAxisTickFormatter = if (compactAxis) { value -> "${(value / 10f).formatOneDecimal()}" } else null,
        tooltipValueFormatter = { value -> value.toInt().toString() }
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        ChartScreenHeader(
            title = "Line Chart Demo",
            description = "Animated multi-series trend chart with legends, scrolling, rotated labels, and tooltips.",
            onBack = onBack
        )

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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = showNullGaps, onCheckedChange = { showNullGaps = it })
            Text("Preserve null gaps")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = compactAxis, onCheckedChange = { compactAxis = it })
            Text("Compact y-axis labels")
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
            shape = RoundedCornerShape(8.dp),
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
