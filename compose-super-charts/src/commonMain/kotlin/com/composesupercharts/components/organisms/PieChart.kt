package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.utils.ChartAccessibility.pieChartDescription
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.atoms.ChartLegendShape
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.models.PieChartData
import com.composesupercharts.models.PieChartStyleConfig
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.atan2
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.components.molecules.UniversalLegend
import com.composesupercharts.components.molecules.LegendItemData
import com.composesupercharts.components.molecules.LegendShape
import com.composesupercharts.models.TooltipBubbleData
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.LegendPosition

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: PieChartData,
    config: PieChartStyleConfig = PieChartStyleConfig()
) {
    if (data.slices.isEmpty()) return

    val density = LocalDensity.current
    val totalValue = data.slices.sumOf { it.value.toDouble() }.toFloat()
    val animationProgress = remember { Animatable(0f) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = config.animationDuration, easing = FastOutSlowInEasing)
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var rotationAngle by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = modifier.fillMaxWidth().semantics { pieChartDescription(data) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (config.legendPosition == LegendPosition.TOP) {
            UniversalLegend(
                items = data.slices.map { LegendItemData(it.label, it.color) },
                textStyle = config.legendTextStyle,
                shape = LegendShape.CIRCLE,
                shapeSize = config.legendShapeSize,
                itemSpacing = config.legendItemSpacing,
                rowSpacing = config.legendRowSpacing,
                contentAlignment = config.legendContentAlignment,
                contentPadding = config.legendContentPadding,
                layoutMode = config.legendLayoutMode,
                showWhenSingleSeries = config.showLegendWhenSingleSeries
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier.size(config.chartSize).padding(16.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .pointerInput(data) {
                    awaitEachGesture {
                        var pastTouchSlop = false
                        val touchSlop = viewConfiguration.touchSlop
                        var zoom = 1f

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
                            } else if (event.changes.size == 1) {
                                // One finger drag for rotation
                                val change = event.changes[0]
                                if (change.pressed && change.previousPressed) {
                                    val dragAmount = change.position - change.previousPosition
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val startAngle = atan2(change.position.y - dragAmount.y - center.y, change.position.x - dragAmount.x - center.x)
                                    val endAngle = atan2(change.position.y - center.y, change.position.x - center.x)
                                    rotationAngle += (endAngle - startAngle).toDegrees()
                                    change.consume()
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxWidth().size(config.chartSize)
                    .pointerInput(data) {
                    detectTapGestures { tap ->
                        val center = Offset(size.width.toFloat() / 2f, size.height.toFloat() / 2f)
                        val dx = tap.x - center.x
                        val dy = tap.y - center.y
                        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                        val radius = size.width.toFloat() / 2f
                        
                        if (distance > radius) {
                            selectedIndex = null
                            return@detectTapGestures
                        }

                        val angle = (atan2(dy.toDouble(), dx.toDouble()) * 180.0 / PI).toFloat()
                        var normalizedAngle = if (angle < 0) angle + 360f else angle
                        
                        var startAngle = config.startAngle + rotationAngle
                        while (startAngle < 0) startAngle += 360f
                        startAngle %= 360f

                        var currentAngle = startAngle
                        data.slices.forEachIndexed { index, slice ->
                            val sweepAngle = (slice.value / totalValue) * 360f
                            val endAngle = (currentAngle + sweepAngle)
                            
                            val isWithin = if (normalizedAngle < currentAngle && currentAngle + sweepAngle > 360f) {
                                normalizedAngle + 360f in currentAngle..endAngle
                            } else {
                                normalizedAngle in currentAngle..endAngle
                            }

                            if (isWithin) {
                                if (selectedIndex != index) {
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                }
                                selectedIndex = if (selectedIndex == index) null else index
                                return@detectTapGestures
                            }
                            currentAngle = (currentAngle + sweepAngle) % 360f
                        }
                    }
                }
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2
                var currentAngle = config.startAngle + rotationAngle

                data.slices.forEachIndexed { index, slice ->
                    val sweepAngle = (slice.value / totalValue) * 360f * animationProgress.value
                    val sliceRadius = radius * slice.radiusRatio
                    val isExploded = selectedIndex == index || slice.offsetRatio > 0f
                    val offsetDistance = if (isExploded) {
                        val ratio = if (selectedIndex == index) config.activeSliceOffsetRatio else slice.offsetRatio
                        radius * ratio
                    } else 0f

                    val midAngle = currentAngle + sweepAngle / 2
                    val midAngleRad = midAngle.toDouble() * PI / 180.0
                    val offsetX = offsetDistance * cos(midAngleRad).toFloat()
                    val offsetY = offsetDistance * sin(midAngleRad).toFloat()

                    val rect = Rect(
                        offset = Offset(center.x - sliceRadius + offsetX, center.y - sliceRadius + offsetY),
                        size = Size(sliceRadius * 2, sliceRadius * 2)
                    )

                    if (config.innerRadiusRatio > 0f) {
                        val innerRadius = sliceRadius * config.innerRadiusRatio
                        val path = Path().apply {
                            arcTo(rect, currentAngle, sweepAngle - config.sliceSpacing, true)
                            val innerRect = Rect(
                                offset = Offset(center.x - innerRadius + offsetX, center.y - innerRadius + offsetY),
                                size = Size(innerRadius * 2, innerRadius * 2)
                            )
                            arcTo(innerRect, currentAngle + sweepAngle - config.sliceSpacing, -(sweepAngle - config.sliceSpacing), false)
                            close()
                        }
                        drawPath(path = path, color = slice.color, style = Fill)
                    } else {
                        drawArc(
                            color = slice.color,
                            startAngle = currentAngle + config.sliceSpacing / 2,
                            sweepAngle = sweepAngle - config.sliceSpacing,
                            useCenter = true,
                            topLeft = rect.topLeft,
                            size = rect.size,
                            style = Fill
                        )
                    }

                    currentAngle += (slice.value / totalValue) * 360f
                }
            }

            if (config.centerLabel != null || config.centerValue != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    config.centerValue?.let {
                        ChartText(text = it, style = config.centerValueTextStyle)
                    }
                    config.centerLabel?.let {
                        ChartText(text = it, style = config.centerLabelTextStyle)
                    }
                }
            }

            selectedIndex?.let { index ->
                val slice = data.slices[index]
                val sweepAngle = (slice.value / totalValue) * 360f
                var startAngle = config.startAngle + rotationAngle
                for (i in 0 until index) {
                    startAngle += (data.slices[i].value / totalValue) * 360f
                }
                val midAngle = startAngle + sweepAngle / 2
                val midAngleRad = midAngle.toDouble() * PI / 180.0
                val chartSizePx = with(density) { config.chartSize.toPx() }
                val radiusPx = chartSizePx / 2f
                val offsetX = (radiusPx * config.tooltipOffsetRatio) * cos(midAngleRad).toFloat()
                val offsetY = (radiusPx * config.tooltipOffsetRatio) * sin(midAngleRad).toFloat()
                val tooltipX = radiusPx + offsetX
                val tooltipY = radiusPx + offsetY

                val isLeftEdge = midAngle > 100f && midAngle < 260f
                val isRightEdge = midAngle < 80f || midAngle > 280f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = with(density) { tooltipY.toDp() - 8.dp })
                        .zIndex(15f)
                ) {
                    TooltipBubble(
                        xPosition = tooltipX,
                        labels = slice.tooltipData ?: listOf(TooltipBubbleData(slice.label, config.valueFormatter?.invoke(slice.value) ?: slice.value.toInt().toString())),
                        isFirst = isLeftEdge,
                        isLast = isRightEdge,
                        config = ChartStyleConfig(
                            lines = emptyList(),
                            tooltipBackgroundColor = config.tooltipBackgroundColor,
                            tooltipBorderColor = config.tooltipBorderColor,
                            tooltipElevation = config.tooltipElevation,
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

        if (config.legendPosition == LegendPosition.BOTTOM) {
            Spacer(modifier = Modifier.height(16.dp))
            UniversalLegend(
                items = data.slices.map { LegendItemData(it.label, it.color) },
                textStyle = config.legendTextStyle,
                shape = LegendShape.CIRCLE,
                shapeSize = config.legendShapeSize,
                itemSpacing = config.legendItemSpacing,
                rowSpacing = config.legendRowSpacing,
                contentAlignment = config.legendContentAlignment,
                contentPadding = config.legendContentPadding,
                layoutMode = config.legendLayoutMode,
                showWhenSingleSeries = config.showLegendWhenSingleSeries
            )
        }
    }
}


private fun Float.toDegrees() = (this * 180f / PI.toFloat())
