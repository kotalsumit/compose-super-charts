package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.utils.ChartAccessibility.scatterChartDescription
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*

@Composable
fun ScatterChart(
    modifier: Modifier = Modifier,
    data: ScatterChartData,
    config: ScatterChartStyleConfig = ScatterChartStyleConfig()
) {
    if (data.series.isEmpty()) return

    val density = LocalDensity.current
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(config.animationDuration))
    }

    var selectedPoint by remember { mutableStateOf<Pair<ScatterSeries, ScatterPoint>?>(null) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    LaunchedEffect(data) {
        selectedPoint = null
    }

    var scaleX by remember { mutableStateOf(1f) }
    var scaleY by remember { mutableStateOf(1f) }
    val scrollStateX = rememberScrollState()
    val scrollStateY = rememberScrollState()

    BoxWithConstraints(modifier = modifier.padding(config.padding).semantics { scatterChartDescription(data) }) {
        val baseWidth = constraints.maxWidth.toFloat()
        val baseHeight = constraints.maxHeight.toFloat()
        
        val width = baseWidth * scaleX
        val height = baseHeight * scaleY

        val allPoints = data.series.flatMap { it.points }
        val minX = allPoints.minOfOrNull { it.x } ?: 0f
        val maxX = allPoints.maxOfOrNull { it.x } ?: 100f
        val minY = allPoints.minOfOrNull { it.y } ?: 0f
        val maxY = allPoints.maxOfOrNull { it.y } ?: 100f

        val xRange = (maxX - minX).coerceAtLeast(1f)
        val yRange = (maxY - minY).coerceAtLeast(1f)

        Box(modifier = Modifier.fillMaxSize()
            .horizontalScroll(scrollStateX)
            .verticalScroll(scrollStateY)
        ) {
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
                            var found: Pair<ScatterSeries, ScatterPoint>? = null
                            data.series.forEach { series ->
                                series.points.forEach { point ->
                                    val px = ((point.x - minX) / xRange) * width
                                    val py = height - ((point.y - minY) / yRange) * height
                                    val distance = (Offset(px, py) - tap).getDistance()
                                    val radius = with(density) { (point.radius ?: config.defaultPointRadius.toPx()) }
                                    if (distance <= radius * 2) {
                                        found = series to point
                                    }
                                }
                            }
                            selectedPoint = if (selectedPoint == found) null else found
                        }
                    }
            ) {
            // Draw Axes
            drawLine(
                color = config.axisColor,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = config.axisThickness.toPx()
            )
            drawLine(
                color = config.axisColor,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = config.axisThickness.toPx()
            )

            // Draw Grid Lines (simplified)
            if (config.showGridLines) {
                val gridCount = 5
                for (i in 1..gridCount) {
                    val y = height - (i.toFloat() / gridCount) * height
                    drawLine(
                        color = config.gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    
                    val x = (i.toFloat() / gridCount) * width
                    drawLine(
                        color = config.gridLineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            // Draw Points
            data.series.forEach { series ->
                series.points.forEach { point ->
                    val px = ((point.x - minX) / xRange) * width
                    val py = height - ((point.y - minY) / yRange) * height
                    val radius = (point.radius ?: config.defaultPointRadius.toPx()) * animationProgress.value
                    
                    drawCircle(
                        color = (point.color ?: series.color).copy(alpha = 0.6f),
                        center = Offset(px, py),
                        radius = radius
                    )
                    drawCircle(
                        color = point.color ?: series.color,
                        center = Offset(px, py),
                        radius = radius,
                        style = Stroke(width = config.pointStrokeWidth.toPx())
                    )
                }
            }
        }

        selectedPoint?.let { selection ->
            val series = selection.first
            val point = selection.second
            val px = ((point.x - minX) / xRange) * width
            val py = height - ((point.y - minY) / yRange) * height
            
            Box(modifier = Modifier.offset(
                x = with(density) { px.toDp() },
                y = with(density) { py.toDp() - (point.radius?.toDp() ?: config.defaultPointRadius) - 8.dp }
            )) {
                TooltipBubble(
                    xPosition = 0f,
                    labels = listOf(
                        TooltipBubbleData(labelName = "Series", value = series.label),
                        TooltipBubbleData(labelName = point.label ?: "Point", value = "X: ${point.x}, Y: ${point.y}")
                    ),
                    isFirst = false,
                    isLast = false,
                    config = ChartStyleConfig(
                        lines = emptyList(),
                        tooltipBackgroundColor = config.tooltipBackgroundColor,
                        tooltipBorderColor = config.tooltipBorderColor,
                        tooltipLabelTextStyle = config.tooltipLabelTextStyle,
                        tooltipValueTextStyle = config.tooltipValueTextStyle,
                        tooltipAutoDismissMs = config.tooltipAutoDismissMs,
                        showTooltipCloseButton = config.showTooltipCloseButton
                    ),
                    onClose = { selectedPoint = null }
                )
            }
        }
    }
    }
}
