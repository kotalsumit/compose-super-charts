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
import com.composesupercharts.utils.ChartAccessibility.candlestickChartDescription
import com.composesupercharts.utils.rotatedLayout
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.composesupercharts.components.atoms.ChartDivider
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*

@Composable
fun CandlestickChart(
    modifier: Modifier = Modifier,
    data: CandlestickChartData,
    config: CandlestickChartStyleConfig = CandlestickChartStyleConfig()
) {
    if (data.entries.isEmpty()) return

    val density = LocalDensity.current
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(config.animationDuration))
    }

    var scaleX by remember { mutableStateOf(1f) }
    val scrollState = rememberScrollState()
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val minVal = data.entries.minOf { it.low }
    val maxVal = data.entries.maxOf { it.high }
    val range = (maxVal - minVal).coerceAtLeast(0.01f)

    BoxWithConstraints(modifier = modifier.semantics { candlestickChartDescription(data) }) {
        val baseWidth = constraints.maxWidth.toFloat()
        val baseHeight = constraints.maxHeight.toFloat()
        val yAxisWidthPx = with(density) { config.yAxisWidth.toPx() }
        val plotBaseWidth = (baseWidth - yAxisWidthPx).coerceAtLeast(1f)

        val width = if (config.isScrollable) {
            with(density) { (config.candleWidth * data.entries.size).toPx() }.coerceAtLeast(plotBaseWidth)
        } else {
            plotBaseWidth * scaleX
        }
        
        val height = baseHeight
        val candleCount = data.entries.size
        val candleWidth = width / candleCount
        val bodyWidth = candleWidth * config.bodyWidthRatio

        Row(modifier = Modifier.fillMaxWidth().height(with(density) { height.toDp() })) {
            // Fixed Y-Axis
            Column(
                modifier = Modifier.width(config.yAxisWidth).fillMaxHeight(),
                horizontalAlignment = Alignment.End
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    val priceLabels = (0..5).map { i -> (minVal + (5 - i) * (range / 5)).toInt().toString() }
                    priceLabels.forEach { label ->
                        com.composesupercharts.components.atoms.ChartText(
                            text = label,
                            style = config.yAxisLabelTextStyle,
                            modifier = Modifier.padding(end = 8.dp).offset(y = (-8).dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
            }

            val scrollModifier = if (config.isScrollable || scaleX > 1f) {
                Modifier.horizontalScroll(scrollState)
            } else {
                Modifier
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight().then(scrollModifier)) {
                Column {
                    Canvas(
                        modifier = Modifier
                            .requiredWidth(with(density) { width.toDp() })
                            .weight(1f)
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
                        // Grid lines
                        for (i in 0..5) {
                            val y = size.height - (i / 5f) * size.height
                            drawLine(Color.LightGray.copy(alpha = 0.3f), Offset(0f, y), Offset(size.width, y), 0.5.dp.toPx())
                        }

                        data.entries.forEachIndexed { index, entry ->
                            val isBullish = entry.close >= entry.open
                            val color = if (isBullish) config.bullishColor else config.bearishColor
                            val centerX = index * candleWidth + candleWidth / 2
                            
                            val lowY = size.height - ((entry.low - minVal) / range) * size.height * animationProgress.value
                            val highY = size.height - ((entry.high - minVal) / range) * size.height * animationProgress.value
                            val openY = size.height - ((entry.open - minVal) / range) * size.height * animationProgress.value
                            val closeY = size.height - ((entry.close - minVal) / range) * size.height * animationProgress.value
                            
                            drawLine(color, Offset(centerX, highY), Offset(centerX, lowY), config.wickWidth.toPx())
                            val bodyTop = openY.coerceAtMost(closeY)
                            val bodyBottom = openY.coerceAtLeast(closeY)
                            val bodyHeight = (bodyBottom - bodyTop).coerceAtLeast(1f)
                            drawRect(color, Offset(centerX - bodyWidth / 2, bodyTop), Size(bodyWidth, bodyHeight))
                        }
                    }

                    ChartDivider(color = config.axisColor, thickness = config.axisThickness)
                    // X-Axis Labels Row (Scrollable)
                    Row(
                        modifier = Modifier.width(with(density) { width.toDp() }).height(48.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        data.entries.forEachIndexed { index, entry ->
                            Box(
                                modifier = Modifier.width(with(density) { candleWidth.toDp() }),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (index % (candleCount / 5).coerceAtLeast(1) == 0) {
                                    val safeOffset = when {
                                        index == 0 -> 20.dp
                                        index == data.entries.size - 1 -> (-20).dp
                                        else -> 0.dp
                                    }
                                    com.composesupercharts.components.atoms.ChartText(
                                        text = entry.label,
                                        style = config.xAxisLabelTextStyle,
                                        modifier = Modifier
                                            .wrapContentWidth(unbounded = true)
                                            .offset(x = safeOffset)
                                            .padding(top = 8.dp)
                                            .rotatedLayout(config.xAxisLabelRotation)
                                    )
                                }
                            }
                        }
                    }
                }

                selectedIndex?.let { index ->
                    val entry = data.entries[index]
                    val centerX = index * candleWidth + candleWidth / 2
                    val chartHeightPx = with(density) { (height - 48).coerceAtLeast(100f) }
                    val highY = chartHeightPx - ((entry.high - minVal) / range) * chartHeightPx

                    Box(modifier = Modifier
                        .width(with(density) { width.toDp() })
                        .offset(
                            x = 0.dp,
                            y = with(density) { highY.toDp() - 8.dp }
                        )
                        .zIndex(15f)
                    ) {
                        TooltipBubble(
                            xPosition = centerX,
                            labels = listOf(
                                TooltipBubbleData(labelName = "Open", value = entry.open.toString()),
                                TooltipBubbleData(labelName = "High", value = entry.high.toString()),
                                TooltipBubbleData(labelName = "Low", value = entry.low.toString()),
                                TooltipBubbleData(labelName = "Close", value = entry.close.toString())
                            ),
                            isFirst = index == 0,
                            isLast = index == data.entries.size - 1,
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
}
