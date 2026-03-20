package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*

@Composable
fun BubbleChart(
    modifier: Modifier = Modifier,
    data: BubbleChartData,
    config: BubbleChartStyleConfig = BubbleChartStyleConfig()
) {
    if (data.points.isEmpty()) return

    val density = LocalDensity.current
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(1000))
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(data) {
        selectedIndex = null
    }

    val minX = remember(data) { data.points.minOf { it.x } }
    val maxX = remember(data) { data.points.maxOf { it.x } }
    val minY = remember(data) { data.points.minOf { it.y } }
    val maxY = remember(data) { data.points.maxOf { it.y } }
    val minSize = remember(data) { data.points.minOf { it.size } }
    val maxSize = remember(data) { data.points.maxOf { it.size } }

    val xRange = maxX - minX
    val yRange = maxY - minY
    val sizeRange = maxSize - minSize

    BoxWithConstraints(modifier = modifier.padding(config.padding)) {
        val maxRadiusPx = with(density) { config.maxBubbleRadius.toPx() }
        val chartWidth = (constraints.maxWidth.toFloat() - maxRadiusPx * 2).coerceAtLeast(0f)
        val chartHeight = (constraints.maxHeight.toFloat() - maxRadiusPx * 2).coerceAtLeast(0f)

        Canvas(
            modifier = Modifier.fillMaxSize()
                .pointerInput(data) {
                    detectTapGestures { tap ->
                        tapOffset = tap
                        var foundIndex: Int? = null
                        data.points.forEachIndexed { index, point ->
                            val normalizedX = if (xRange == 0f) 0.5f else (point.x - minX) / xRange
                            val normalizedY = if (yRange == 0f) 0.5f else (point.y - minY) / yRange
                            val normalizedSize = if (sizeRange == 0f) 1.0f else 0.2f + 0.8f * ((point.size - minSize) / sizeRange)
                            
                            val centerX = maxRadiusPx + (normalizedX * chartWidth)
                            val centerY = chartHeight + maxRadiusPx - (normalizedY * chartHeight)
                            val radius = normalizedSize * maxRadiusPx

                            val distance = androidx.compose.ui.geometry.Offset(centerX, centerY).minus(tap).getDistance()
                            if (distance <= radius) {
                                foundIndex = index
                            }
                        }
                        selectedIndex = if (selectedIndex == foundIndex) null else foundIndex
                    }
                }
        ) {
            if (config.showGridLines) {
                val gridSteps = 5
                for (i in 0..gridSteps) {
                    val y = chartHeight + maxRadiusPx - (i.toFloat() / gridSteps * chartHeight)
                    drawLine(config.gridLinesColor, Offset(maxRadiusPx, y), Offset(chartWidth + maxRadiusPx, y))
                    
                    val x = maxRadiusPx + (i.toFloat() / gridSteps * chartWidth)
                    drawLine(config.gridLinesColor, Offset(x, maxRadiusPx), Offset(x, chartHeight + maxRadiusPx))
                }
            }

            drawLine(config.axisColor, Offset(maxRadiusPx, chartHeight + maxRadiusPx), Offset(chartWidth + maxRadiusPx, chartHeight + maxRadiusPx), 2f)
            drawLine(config.axisColor, Offset(maxRadiusPx, maxRadiusPx), Offset(maxRadiusPx, chartHeight + maxRadiusPx), 2f)

            data.points.forEachIndexed { _, point ->
                val normalizedX = if (xRange == 0f) 0.5f else (point.x - minX) / xRange
                val normalizedY = if (yRange == 0f) 0.5f else (point.y - minY) / yRange
                val normalizedSize = if (sizeRange == 0f) 1.0f else 0.2f + 0.8f * ((point.size - minSize) / sizeRange)
                
                val centerX = maxRadiusPx + (normalizedX * chartWidth)
                val centerY = chartHeight + maxRadiusPx - (normalizedY * chartHeight)
                val radius = normalizedSize * maxRadiusPx * animationProgress.value

                drawCircle(
                    color = point.color.copy(alpha = config.bubbleOpacity),
                    center = Offset(centerX, centerY),
                    radius = radius
                )
                
                drawCircle(
                    color = point.color,
                    center = Offset(centerX, centerY),
                    radius = radius,
                    style = Stroke(width = config.bubbleStrokeWidth.toPx())
                )
            }
        }

        selectedIndex?.let { index ->
            val point = data.points[index]
            val normalizedX = if (xRange == 0f) 0.5f else (point.x - minX) / xRange
            val normalizedY = if (yRange == 0f) 0.5f else (point.y - minY) / yRange
            val normalizedSize = if (sizeRange == 0f) 1.0f else 0.2f + 0.8f * ((point.size - minSize) / sizeRange)
            val centerX = maxRadiusPx + (normalizedX * chartWidth)
            val centerY = chartHeight + maxRadiusPx - (normalizedY * chartHeight)
            val radius = normalizedSize * maxRadiusPx

            Box(modifier = Modifier.offset(
                x = with(density) { centerX.toDp() },
                y = with(density) { centerY.toDp() - radius.toDp() - 8.dp }
            )) {
                TooltipBubble(
                    xPosition = 0f,
                    labels = point.tooltipData ?: listOf(
                        TooltipBubbleData(point.label, "${point.size}")
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
