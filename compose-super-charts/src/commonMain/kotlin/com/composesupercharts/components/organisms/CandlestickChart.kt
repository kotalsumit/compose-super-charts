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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.utils.ChartAccessibility.candlestickChartDescription
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*

@Composable
fun CandlestickChart(
    modifier: Modifier = Modifier,
    data: CandlestickChartData,
    config: CandlestickChartStyleConfig = CandlestickChartStyleConfig()
) {
    if (data.entries.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(config.animationDuration))
    }

    var scaleX by remember { mutableStateOf(1f) }
    val scrollState = rememberScrollState()

    val minVal = data.entries.minOf { it.low }
    val maxVal = data.entries.maxOf { it.high }
    val range = (maxVal - minVal).coerceAtLeast(0.01f)

    BoxWithConstraints(modifier = modifier.padding(config.padding).semantics { candlestickChartDescription(data) }) {
        val baseWidth = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        
        val width = baseWidth * scaleX
        val candleCount = data.entries.size
        val candleWidth = width / candleCount
        val bodyWidth = candleWidth * config.bodyWidthRatio

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize().horizontalScroll(scrollState)) {
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
                                    if (zoomChange != 1f) {
                                        scaleX = (scaleX * zoomChange).coerceIn(1f, 10f)
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .pointerInput(data) {
                    detectTapGestures { tap ->
                        val index = (tap.x / candleWidth).toInt().coerceIn(0, candleCount - 1)
                        selectedIndex = if (selectedIndex == index) null else index
                    }
                }
        ) {
            // Axes
            drawLine(config.axisColor, Offset(0f, height), Offset(width, height), config.axisThickness.toPx())
            drawLine(config.axisColor, Offset(0f, 0f), Offset(0f, height), config.axisThickness.toPx())

            data.entries.forEachIndexed { index, entry ->
                val isBullish = entry.close >= entry.open
                val color = if (isBullish) config.bullishColor else config.bearishColor
                
                val centerX = index * candleWidth + candleWidth / 2
                
                val lowY = height - ((entry.low - minVal) / range) * height * animationProgress.value
                val highY = height - ((entry.high - minVal) / range) * height * animationProgress.value
                val openY = height - ((entry.open - minVal) / range) * height * animationProgress.value
                val closeY = height - ((entry.close - minVal) / range) * height * animationProgress.value
                
                // Wick
                drawLine(color, Offset(centerX, highY), Offset(centerX, lowY), config.wickWidth.toPx())
                
                // Body
                val bodyTop = openY.coerceAtMost(closeY)
                val bodyBottom = openY.coerceAtLeast(closeY)
                val bodyHeight = (bodyBottom - bodyTop).coerceAtLeast(1f)
                
                drawRect(
                    color = color,
                    topLeft = Offset(centerX - bodyWidth / 2, bodyTop),
                    size = Size(bodyWidth, bodyHeight)
                )

                // X-Axis Labels (Simple sampling)
                if (index % (candleCount / 5).coerceAtLeast(1) == 0) {
                    val labelLayout = textMeasurer.measure(entry.label, config.labelTextStyle)
                    drawText(labelLayout, topLeft = Offset(centerX - labelLayout.size.width / 2, height + 4.dp.toPx()))
                }
            }
        }

        selectedIndex?.let { index ->
            val entry = data.entries[index]
            val centerX = index * candleWidth + candleWidth / 2
            val highY = height - ((entry.high - minVal) / range) * height

            Box(modifier = Modifier.offset(
                x = with(density) { centerX.toDp() },
                y = with(density) { highY.toDp() - 8.dp }
            )) {
                TooltipBubble(
                    xPosition = 0f,
                    labels = listOf(
                        TooltipBubbleData(labelName = "Open", value = entry.open.toString()),
                        TooltipBubbleData(labelName = "High", value = entry.high.toString()),
                        TooltipBubbleData(labelName = "Low", value = entry.low.toString()),
                        TooltipBubbleData(labelName = "Close", value = entry.close.toString())
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
                    onClose = { selectedIndex = null }
                )
            }
        }
    }
    }
}
