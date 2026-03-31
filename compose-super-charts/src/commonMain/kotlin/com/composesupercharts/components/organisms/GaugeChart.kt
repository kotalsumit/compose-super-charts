package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.TooltipBubbleData
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.GaugeChartData
import com.composesupercharts.models.GaugeChartStyleConfig
import com.composesupercharts.utils.ChartAccessibility.gaugeChartDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun GaugeChart(
    modifier: Modifier = Modifier,
    data: GaugeChartData,
    config: GaugeChartStyleConfig = GaugeChartStyleConfig()
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    
    val animationProgress = remember { Animatable(0f) }
    
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showTooltip by remember { mutableStateOf(false) }

    LaunchedEffect(data.currentValue) {
        animationProgress.animateTo(
            targetValue = (data.currentValue - data.minValue) / (data.maxValue - data.minValue),
            animationSpec = tween(config.animationDuration)
        )
    }

    BoxWithConstraints(
        modifier = modifier.padding(config.padding).semantics { gaugeChartDescription(data) },
        contentAlignment = Alignment.Center
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val size = width.coerceAtMost(height)
        val arcRadius = (size / 2) - with(density) { config.arcThickness.toPx() / 2 }
        val center = Offset(width / 2, height / 2)

        Canvas(
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
                    detectTapGestures {
                        showTooltip = !showTooltip
                    }
                }
        ) {
            drawArc(
                color = config.backgroundArcColor,
                startAngle = config.startAngle,
                sweepAngle = config.sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = config.arcThickness.toPx(), cap = StrokeCap.Round)
            )

            data.ranges.forEach { range ->
                val rangeStartAngle = config.startAngle + (range.min - data.minValue) / (data.maxValue - data.minValue) * config.sweepAngle
                val rangeSweepAngle = (range.max - range.min) / (data.maxValue - data.minValue) * config.sweepAngle
                
                drawArc(
                    color = range.color,
                    startAngle = rangeStartAngle,
                    sweepAngle = rangeSweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = config.arcThickness.toPx(), cap = StrokeCap.Butt)
                )
            }

            val currentAngle = config.startAngle + animationProgress.value * config.sweepAngle
            val angleRad = (currentAngle * PI / 180f).toDouble()
            
            val needleLength = arcRadius * 0.9f
            val needleEnd = Offset(
                center.x + (cos(angleRad) * needleLength).toFloat(),
                center.y + (sin(angleRad) * needleLength).toFloat()
            )

            val needlePath = Path().apply {
                val backAngle1 = ((currentAngle + 90) * PI / 180f).toDouble()
                val backAngle2 = ((currentAngle - 90) * PI / 180f).toDouble()
                val baseWidth = config.needleWidth.toPx()
                
                moveTo(needleEnd.x, needleEnd.y)
                lineTo(
                    center.x + (cos(backAngle1) * baseWidth).toFloat(),
                    center.y + (sin(backAngle1) * baseWidth).toFloat()
                )
                lineTo(
                    center.x + (cos(backAngle2) * baseWidth).toFloat(),
                    center.y + (sin(backAngle2) * baseWidth).toFloat()
                )
                close()
            }

            drawArc(
                color = config.valueArcColor,
                startAngle = config.startAngle,
                sweepAngle = animationProgress.value * config.sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = config.arcThickness.toPx(), cap = StrokeCap.Round)
            )

            drawPath(
                path = needlePath,
                color = config.needleColor
            )

            drawCircle(
                color = config.needleColor,
                radius = config.needleBaseRadius.toPx(),
                center = center
            )

            val valueText = data.currentValue.toInt().toString()
            val unitText = data.unit ?: ""
            
            val valueLayout = textMeasurer.measure(valueText, config.valueTextStyle)
            val unitLayout = textMeasurer.measure(unitText, config.unitTextStyle)
            
            drawText(
                valueLayout,
                topLeft = Offset(
                    center.x - valueLayout.size.width / 2,
                    center.y + config.needleBaseRadius.toPx() + 8.dp.toPx()
                )
            )
            
            drawText(
                unitLayout,
                topLeft = Offset(
                    center.x - unitLayout.size.width / 2,
                    center.y + config.needleBaseRadius.toPx() + 8.dp.toPx() + valueLayout.size.height
                )
            )
        }

        if (showTooltip) {
            TooltipBubble(
                xPosition = 0f,
                labels = listOf(TooltipBubbleData(labelName = "Current Value", value = data.currentValue.toString())),
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
                onClose = { showTooltip = false }
            )
        }
    }
}
