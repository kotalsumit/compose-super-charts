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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*
import com.composesupercharts.utils.ChartAccessibility.vennDiagramDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.PI

@Composable
fun VennDiagram(
    modifier: Modifier = Modifier,
    data: VennDiagramData,
    config: VennDiagramStyleConfig = VennDiagramStyleConfig()
) {
    if (data.sets.isEmpty()) return

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(durationMillis = config.animationDuration, easing = FastOutSlowInEasing))
    }

    var selectedSetIndex by remember { mutableStateOf<Int?>(null) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(data) {
        selectedSetIndex = null
    }

    BoxWithConstraints(modifier = modifier.padding(config.padding).semantics { vennDiagramDescription(data) }) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val displacementFactor = when (data.sets.size) {
            1 -> 0f
            2 -> 0.6f
            3 -> 0.55f
            else -> 0.8f
        }
        
        val maxRadius = (width.coerceAtMost(height) / 2) / (displacementFactor + 1f)
        val radius = maxRadius * config.circleRadiusScale * animationProgress.value

        val circleCenters = remember(data.sets.size, width, height, radius) {
            when (data.sets.size) {
                1 -> listOf(Offset(width / 2, height / 2))
                2 -> {
                    val distance = radius * 1.2f
                    listOf(
                        Offset(width / 2 - distance / 2, height / 2),
                        Offset(width / 2 + distance / 2, height / 2)
                    )
                }
                3 -> {
                    val distance = radius * 1.1f
                    val centerX = width / 2
                    val centerY = height / 2
                    listOf(
                        Offset(centerX, centerY - distance * 0.55f),
                        Offset(centerX - distance * 0.5f, centerY + distance * 0.3f),
                        Offset(centerX + distance * 0.5f, centerY + distance * 0.3f)
                    )
                }
                else -> {
                    data.sets.indices.map { i ->
                        val angle = (i.toFloat() / data.sets.size) * 2 * PI.toFloat()
                        Offset(
                            width / 2 + cos(angle.toDouble()).toFloat() * radius * 0.8f,
                            height / 2 + sin(angle.toDouble()).toFloat() * radius * 0.8f
                        )
                    }
                }
            }
        }

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
                    detectTapGestures { tap ->
                        tapOffset = tap
                        var foundIndex: Int? = null
                        circleCenters.forEachIndexed { index, center ->
                            val distance = (center - tap).getDistance()
                            if (distance <= radius) {
                                foundIndex = index
                            }
                        }
                        selectedSetIndex = if (selectedSetIndex == foundIndex) null else foundIndex
                    }
                }
        ) {
            data.sets.forEachIndexed { index, set ->
                val center = circleCenters[index]
                drawCircle(
                    color = set.color.copy(alpha = config.circleOpacity),
                    center = center,
                    radius = radius
                )
                drawCircle(
                    color = set.color,
                    center = center,
                    radius = radius,
                    style = Stroke(width = config.circleStrokeWidth.toPx())
                )

                if (config.showLabels) {
                    val textLayoutResult = textMeasurer.measure(set.label, config.labelTextStyle)
                    val textOffset = Offset(
                        center.x - textLayoutResult.size.width / 2,
                        center.y - textLayoutResult.size.height / 2
                    )
                    drawText(textLayoutResult, topLeft = textOffset)
                }
            }
        }

        selectedSetIndex?.let { index ->
            val set = data.sets[index]
            val center = circleCenters[index]

            Box(modifier = Modifier.offset(
                x = with(density) { center.x.toDp() },
                y = with(density) { center.y.toDp() - radius.toDp() - 8.dp }
            )) {
                TooltipBubble(
                    xPosition = 0f,
                    labels = listOf(TooltipBubbleData(set.label, set.value.toString())),
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
                    onClose = { selectedSetIndex = null }
                )
            }
        }
    }
}
