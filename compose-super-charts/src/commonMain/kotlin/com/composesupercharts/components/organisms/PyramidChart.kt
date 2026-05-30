package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.components.molecules.LegendItemData
import com.composesupercharts.components.molecules.LegendShape
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.components.molecules.UniversalLegend
import com.composesupercharts.models.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PyramidChart(
    modifier: Modifier = Modifier,
    data: PyramidChartData,
    config: PyramidChartStyleConfig = PyramidChartStyleConfig()
) {
    if (data.segments.isEmpty()) return

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = config.animationDuration, easing = FastOutSlowInEasing)
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    LaunchedEffect(data) {
        selectedIndex = null
    }

    val totalValue = remember(data) { data.segments.sumOf { it.value.toDouble().coerceAtLeast(0.0) }.toFloat().coerceAtLeast(1f) }
    
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp)) {
            val chartWidth = constraints.maxWidth.toFloat()
            val chartHeight = constraints.maxHeight.toFloat()
            val progress = animationProgress.value

            var tapOffset by remember { mutableStateOf(Offset.Zero) }
            Box(
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .pointerInput(data) {
                        awaitEachGesture {
                            var zoom = 1f
                            var pastTouchSlop = false
                            val touchSlop = viewConfiguration.touchSlop

                            do {
                                val event = awaitPointerEvent()
                                val isMultiTouch = event.changes.size > 1

                                if (isMultiTouch) {
                                    val zoomChange = event.calculateZoom()
                                    val panChange = event.calculatePan()

                                    if (!pastTouchSlop) {
                                        zoom *= zoomChange
                                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                                        val zoomMotion = kotlin.math.abs(1 - zoom) * centroidSize
                                        if (zoomMotion > touchSlop) pastTouchSlop = true
                                    }

                                    if (pastTouchSlop) {
                                        scale = (scale * zoomChange).coerceIn(1f, 10f)
                                        offset += panChange
                                        event.changes.forEach { it.consume() }
                                    }
                                }
                            } while (event.changes.any { it.pressed })
                        }
                    }
                    .pointerInput(data) {
                        detectTapGestures { tap ->
                            tapOffset = tap
                            var currentY = 0f
                            val spacingPx = with(density) { config.spacing.toPx() }
                            val totalSpacing = spacingPx * (data.segments.size - 1)
                            val availableHeight = (chartHeight - totalSpacing).coerceAtLeast(0f)
                            val segmentHeights = data.segments.map { (it.value / totalValue) * availableHeight }
                            
                            var foundIndex: Int? = null
                            for (i in data.segments.indices) {
                                val h = segmentHeights[i]
                                if (tap.y >= currentY && tap.y <= currentY + h) {
                                    val localY = tap.y - currentY
                                    val topY = currentY.coerceIn(0f, chartHeight)
                                    val bottomY = (currentY + h).coerceIn(0f, chartHeight)
                                    
                                    val yFactorTop = if (config.type == PyramidChartType.PYRAMID) topY / chartHeight else (chartHeight - topY) / chartHeight
                                    val yFactorBottom = if (config.type == PyramidChartType.PYRAMID) bottomY / chartHeight else (chartHeight - bottomY) / chartHeight
                                    
                                    val topWidth = yFactorTop * chartWidth
                                    val bottomWidth = yFactorBottom * chartWidth
                                    
                                    val currentWidth = topWidth + (bottomWidth - topWidth) * (localY / h)
                                    val leftBound = (chartWidth - currentWidth) / 2
                                    val rightBound = leftBound + currentWidth
                                    
                                    if (tap.x >= leftBound && tap.x <= rightBound) {
                                        foundIndex = i
                                        break
                                    }
                                }
                                currentY += h + spacingPx
                            }
                            selectedIndex = if (selectedIndex == foundIndex) null else foundIndex
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var currentY = 0f
                    val spacingPx = config.spacing.toPx()
                    val totalSpacing = spacingPx * (data.segments.size - 1)
                    val availableHeight = (size.height - totalSpacing).coerceAtLeast(0f)
                    val segmentHeights = data.segments.map { (it.value.coerceAtLeast(0f) / totalValue) * availableHeight }

                    data.segments.forEachIndexed { index, segment ->
                        val h = segmentHeights[index] * progress
                        if (h <= 0f) return@forEachIndexed

                        val topY = currentY.coerceIn(0f, size.height)
                        val bottomY = (currentY + h).coerceIn(0f, size.height)

                        val yFactorTop = if (config.type == PyramidChartType.PYRAMID) topY / size.height else (size.height - topY) / size.height
                        val yFactorBottom = if (config.type == PyramidChartType.PYRAMID) bottomY / size.height else (size.height - bottomY) / size.height

                        val topWidth = yFactorTop * size.width
                        val bottomWidth = yFactorBottom * size.width

                        val topLeft = Offset((size.width - topWidth) / 2, currentY)
                        val topRight = Offset(topLeft.x + topWidth, currentY)
                        val bottomLeft = Offset((size.width - bottomWidth) / 2, currentY + h)
                        val bottomRight = Offset(bottomLeft.x + bottomWidth, currentY + h)

                        val path = Path().apply {
                            moveTo(topLeft.x, topLeft.y)
                            lineTo(topRight.x, topRight.y)
                            lineTo(bottomRight.x, bottomRight.y)
                            lineTo(bottomLeft.x, bottomLeft.y)
                            close()
                        }

                        drawPath(path, color = segment.color)

                        if (config.showLabels && progress > 0.8f) {
                            val labelText = "${segment.label} (${(segment.value / totalValue * 100).toInt()}%)"
                            
                            val luminance = (0.299 * segment.color.red + 0.587 * segment.color.green + 0.114 * segment.color.blue)
                            val textColor = if (luminance > 0.5) Color.Black else Color.White
                            val textStyle = config.segmentLabelTextStyle.copy(color = textColor)
                            
                            val textLayoutResult = textMeasurer.measure(labelText, textStyle)
                            
                            val currentWidth = (topWidth + bottomWidth) / 2
                            if (currentWidth > textLayoutResult.size.width && h > textLayoutResult.size.height / 2) {
                                val textOffset = Offset(
                                    (size.width - textLayoutResult.size.width) / 2,
                                    currentY + (h - textLayoutResult.size.height) / 2
                                )
                                drawText(textLayoutResult, topLeft = textOffset)
                            }
                        }

                        currentY += h + spacingPx
                    }
                }

                selectedIndex?.let { index ->
                    val segment = data.segments[index]
                    val spacingPx = with(density) { config.spacing.toPx() }
                    val totalSpacing = spacingPx * (data.segments.size - 1)
                    val availableHeight = (chartHeight - totalSpacing).coerceAtLeast(0f)
                    val segmentHeights = data.segments.map { (it.value / totalValue) * availableHeight }
                    var currentY = 0f
                    for (i in 0 until index) {
                        currentY += segmentHeights[i] + spacingPx
                    }
                    val h = segmentHeights[index]
                    val centerY = currentY + h / 2

                    Box(modifier = Modifier.offset(
                        x = with(density) { tapOffset.x.toDp() },
                        y = with(density) { centerY.toDp() - 40.dp }
                    )) {
                        TooltipBubble(
                            xPosition = 0f,
                            labels = segment.tooltipData ?: listOf(TooltipBubbleData(segment.label, segment.value.toInt().toString())),
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
                            onClose = { selectedIndex = null }
                        )
                    }
                }
            }
        }

        if (config.legendPosition != LegendPosition.HIDDEN && (data.segments.size > 1 || config.showLegendWhenSingleSeries)) {
            PyramidLegend(data = data, config = config)
        }
    }
}

@Composable
private fun PyramidLegend(data: PyramidChartData, config: PyramidChartStyleConfig) {
    UniversalLegend(
        items = data.segments.map { segment -> LegendItemData(segment.label, segment.color) },
        textStyle = config.legendTextStyle,
        shape = LegendShape.SQUARE,
        shapeSize = 12.dp,
        itemSpacing = config.legendItemSpacing,
        rowSpacing = config.legendRowSpacing,
        contentAlignment = config.legendContentAlignment,
        contentPadding = config.legendContentPadding,
        layoutMode = config.legendLayoutMode,
        showWhenSingleSeries = config.showLegendWhenSingleSeries
    )
}
