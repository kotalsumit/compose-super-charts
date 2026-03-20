package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    modifier: Modifier = Modifier,
    data: RadarChartData,
    config: RadarChartStyleConfig = RadarChartStyleConfig()
) {
    if (data.series.isEmpty() || data.axisLabels.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(config.animationDuration))
    }

    var selectedEntry by remember { mutableStateOf<Pair<RadarSeries, Int>?>(null) }
    
    val axisCount = data.axisLabels.size
    val angleStep = (2 * PI / axisCount).toFloat()
    val maxValue = data.maxValue ?: data.series.flatMap { it.entries }.maxOfOrNull { it.value } ?: 100f

    BoxWithConstraints(modifier = modifier.padding(config.padding)) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val radius = width.coerceAtMost(height) / 2
        val center = Offset(width / 2, height / 2)

        Canvas(
            modifier = Modifier.fillMaxSize()
                .pointerInput(data) {
                    detectTapGestures { tap ->
                        var found: Pair<RadarSeries, Int>? = null
                        data.series.forEach { series ->
                            series.entries.forEachIndexed { index, entry ->
                                val entryRadius = (entry.value / maxValue) * radius
                                val angle = index * angleStep - (PI / 2).toFloat()
                                val px = center.x + cos(angle) * entryRadius
                                val py = center.y + sin(angle) * entryRadius
                                
                                val distance = (Offset(px, py) - tap).getDistance()
                                if (distance <= 20.dp.toPx()) {
                                    found = series to index
                                }
                            }
                        }
                        selectedEntry = if (selectedEntry == found) null else found
                    }
                }
        ) {
            // Draw Web
            for (level in 1..config.levels) {
                val levelRadius = (level.toFloat() / config.levels) * radius
                if (config.webType == RadarWebType.POLYGON) {
                    val path = Path()
                    for (i in 0 until axisCount) {
                        val angle = i * angleStep - (PI / 2).toFloat()
                        val x = center.x + cos(angle) * levelRadius
                        val y = center.y + sin(angle) * levelRadius
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()
                    drawPath(path, config.webLineColor, style = Stroke(config.webLineWidth.toPx()))
                } else {
                    drawCircle(config.webLineColor, levelRadius, center, style = Stroke(config.webLineWidth.toPx()))
                }
            }

            // Draw Axes
            for (i in 0 until axisCount) {
                val angle = i * angleStep - (PI / 2).toFloat()
                val x = center.x + cos(angle) * radius
                val y = center.y + sin(angle) * radius
                drawLine(config.axisLineColor, center, Offset(x, y), config.axisLineWidth.toPx())
                
                // Labels
                val label = data.axisLabels[i]
                val labelLayout = textMeasurer.measure(label, config.labelTextStyle)
                val labelX = center.x + cos(angle) * (radius + 20.dp.toPx()) - labelLayout.size.width / 2
                val labelY = center.y + sin(angle) * (radius + 20.dp.toPx()) - labelLayout.size.height / 2
                drawText(labelLayout, topLeft = Offset(labelX, labelY))
            }

            // Draw Series
            data.series.forEach { series ->
                val path = Path()
                series.entries.forEachIndexed { index, entry ->
                    val entryRadius = (entry.value / maxValue) * radius * animationProgress.value
                    val angle = index * angleStep - (PI / 2).toFloat()
                    val x = center.x + cos(angle) * entryRadius
                    val y = center.y + sin(angle) * entryRadius
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, series.color.copy(alpha = series.fillAlpha))
                drawPath(path, series.color, style = Stroke(2.dp.toPx()))
            }
        }

        selectedEntry?.let { (series, index) ->
            val entry = series.entries[index]
            val entryRadius = (entry.value / maxValue) * radius
            val angle = index * angleStep - (PI / 2).toFloat()
            val px = center.x + cos(angle) * entryRadius
            val py = center.y + sin(angle) * entryRadius

            Box(modifier = Modifier.offset(
                x = with(density) { px.toDp() },
                y = with(density) { py.toDp() - 8.dp }
            )) {
                TooltipBubble(
                    xPosition = 0f,
                    labels = listOf(
                        TooltipBubbleData(labelName = series.label, value = entry.label ?: entry.value.toString())
                    ),
                    isFirst = false,
                    isLast = false,
                    config = ChartStyleConfig(
                        lines = emptyList(),
                        tooltipBackgroundColor = config.tooltipBackgroundColor,
                        tooltipBorderColor = config.tooltipBorderColor,
                        tooltipLabelTextStyle = config.tooltipLabelTextStyle,
                        tooltipValueTextStyle = config.tooltipValueTextStyle
                    )
                )
            }
        }
    }
}
