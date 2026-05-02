package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.utils.ChartAccessibility.bubbleChartDescription
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*
import com.composesupercharts.utils.rotatedLayout

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
        animationProgress.animateTo(1f, tween(durationMillis = config.animationDuration, easing = FastOutSlowInEasing))
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(data) {
        selectedIndex = null
    }

    var scaleX by remember { mutableStateOf(1f) }
    var scaleY by remember { mutableStateOf(1f) }
    val scrollStateX = rememberScrollState()
    val scrollStateY = rememberScrollState()

    val minX = remember(data) { data.points.minOf { it.x } }
    val maxX = remember(data) { data.points.maxOf { it.x } }
    val minY = remember(data) { data.points.minOf { it.y } }
    val maxY = remember(data) { data.points.maxOf { it.y } }
    val minSize = remember(data) { data.points.minOf { it.size } }
    val maxSize = remember(data) { data.points.maxOf { it.size } }

    val xRange = maxX - minX
    val yRange = maxY - minY
    val sizeRange = maxSize - minSize

    Column(modifier = modifier.padding(config.padding).semantics { bubbleChartDescription(data) }) {
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val baseWidth = constraints.maxWidth.toFloat()
            val baseHeight = constraints.maxHeight.toFloat()
            
            val maxRadiusPx = with(density) { config.maxBubbleRadius.toPx() }
            val width = baseWidth * scaleX
            val height = baseHeight * scaleY
            
            val chartWidth = (width - maxRadiusPx * 2).coerceAtLeast(0f)
            val chartHeight = (height - maxRadiusPx * 2).coerceAtLeast(0f)

            Box(modifier = Modifier.fillMaxSize()
                .horizontalScroll(scrollStateX)
                .verticalScroll(scrollStateY)
            ) {
                Column {
                    Canvas(
                        modifier = Modifier
                            .requiredWidth(with(density) { width.toDp() })
                            .requiredHeight(with(density) { height.toDp() })
                            .pointerInput(data) {
                                awaitEachGesture {
                                    var zoom = 1f
                                    var pastTouchSlop = false
                                    val touchSlop = viewConfiguration.touchSlop

                                    do {
                                        val event = awaitPointerEvent()
                                        val isZooming = event.changes.size > 1
                                        if (isZooming) {
                                            val zoomChange = event.calculateZoom()
                                            if (!pastTouchSlop) {
                                                zoom *= zoomChange
                                                val centroidSize = event.calculateCentroidSize(useCurrent = false)
                                                val zoomMotion = kotlin.math.abs(1 - zoom) * centroidSize
                                                if (zoomMotion > touchSlop) {
                                                    pastTouchSlop = true
                                                }
                                            }

                                            if (pastTouchSlop) {
                                                scaleX = (scaleX * zoomChange).coerceIn(1f, 10f)
                                                scaleY = (scaleY * zoomChange).coerceIn(1f, 10f)
                                                event.changes.forEach { it.consume() }
                                            }
                                        }
                                    } while (event.changes.any { it.pressed })
                                }
                            }
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

                    // X-Axis Labels Row (Sampled)
                    Row(
                        modifier = Modifier.width(with(density) { width.toDp() }),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        val labelCount = 5
                        for (i in 0..labelCount) {
                            val normalizedX = i.toFloat() / labelCount
                            val labelX = minX + (normalizedX * xRange)
                            val centerX = maxRadiusPx + (normalizedX * chartWidth)
                            
                            Box(
                                modifier = Modifier.width(with(density) { (width / (labelCount + 1)).toDp() }).offset(x = with(density) { (centerX - (width / (labelCount + 1)) / 2).toDp() }),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                com.composesupercharts.components.atoms.ChartText(
                                    text = String.format("%.1f", labelX),
                                    style = config.axisLabelTextStyle,
                                    modifier = Modifier
                                        .rotatedLayout(config.xAxisLabelRotation)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
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

                Box(
                    modifier = Modifier
                        .requiredWidth(with(density) { width.toDp() })
                        .offset(y = with(density) { centerY.toDp() - radius.toDp() - 8.dp })
                ) {
                    TooltipBubble(
                        xPosition = centerX,
                        labels = point.tooltipData ?: listOf(
                            TooltipBubbleData(point.label, "${point.size}")
                        ),
                        isFirst = index == 0,
                        isLast = index == data.points.lastIndex,
                        config = ChartStyleConfig(
                            lines = emptyList(),
                            tooltipBackgroundColor = config.tooltipBackgroundColor,
                            tooltipBorderColor = config.tooltipBorderColor,
                            tooltipLabelTextStyle = config.tooltipLabelTextStyle,
                            tooltipValueTextStyle = config.tooltipValueTextStyle,
                            tooltipAutoDismissMs = config.tooltipAutoDismissMs,
                            showTooltipCloseButton = config.showTooltipCloseButton
                        ),
                        onClose = { selectedIndex = null }
                    )
                }
            }
        }
    }
}
