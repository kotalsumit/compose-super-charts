package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*
import com.composesupercharts.utils.ChartAccessibility.heatmapDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun HeatmapChart(
    modifier: Modifier = Modifier,
    data: HeatmapChartData,
    config: HeatmapChartStyleConfig = HeatmapChartStyleConfig()
) {
    if (data.cells.isEmpty()) return

    val density = LocalDensity.current
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(config.animationDuration))
    }

    var scale by remember { mutableStateOf(1f) }
    val scrollStateX = rememberScrollState()
    val scrollStateY = rememberScrollState()

    val minValue = data.minValue ?: data.cells.minOf { it.value }
    val maxValue = data.maxValue ?: data.cells.maxOf { it.value }
    val range = (maxValue - minValue).coerceAtLeast(0.01f)

    BoxWithConstraints(modifier = modifier.padding(config.padding).semantics { heatmapDescription(data) }) {
        val baseWidth = constraints.maxWidth.toFloat()
        val baseHeight = constraints.maxHeight.toFloat()
        
        val width = baseWidth * scale
        val height = baseHeight * scale
        
        val rows = data.cells.maxOf { it.row } + 1
        val cols = data.cells.maxOf { it.col } + 1
        
        val cellWidth = (width - (cols - 1) * with(density) { config.cellSpacing.toPx() }) / cols
        val cellHeight = (height - (rows - 1) * with(density) { config.cellSpacing.toPx() }) / rows
        
        val cellSize = cellWidth.coerceAtMost(cellHeight)

    var selectedCell by remember { mutableStateOf<HeatmapCell?>(null) }

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
                                    if (zoomChange != 1f) {
                                        scale = (scale * zoomChange).coerceIn(1f, 10f)
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .pointerInput(data) {
                    detectTapGestures { tap ->
                        val col = (tap.x / (cellSize + with(density) { config.cellSpacing.toPx() })).toInt()
                        val row = (tap.y / (cellSize + with(density) { config.cellSpacing.toPx() })).toInt()
                        
                        val found = data.cells.find { it.row == row && it.col == col }
                        selectedCell = if (selectedCell == found) null else found
                    }
                }
        ) {
            data.cells.forEach { cell ->
                val normalizedValue = ((cell.value - minValue) / range).coerceIn(0f, 1f)
                val color = lerp(config.startColor, config.endColor, normalizedValue)
                
                val x = cell.col * (cellSize + config.cellSpacing.toPx())
                val y = cell.row * (cellSize + config.cellSpacing.toPx())
                
                drawRoundRect(
                    color = color.copy(alpha = animationProgress.value),
                    topLeft = Offset(x, y),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(config.cornerRadius.toPx(), config.cornerRadius.toPx())
                )
            }
        }

        selectedCell?.let { cell ->
            val x = cell.col * (cellSize + with(density) { config.cellSpacing.toPx() })
            val y = cell.row * (cellSize + with(density) { config.cellSpacing.toPx() })
            
            val rowLabel = data.rowLabels?.getOrNull(cell.row) ?: "Row ${cell.row}"
            val colLabel = data.columnLabels?.getOrNull(cell.col) ?: "Col ${cell.col}"

            Box(modifier = Modifier.offset(
                x = with(density) { (x + cellSize / 2).toDp() },
                y = with(density) { y.toDp() - 8.dp }
            )) {
                TooltipBubble(
                    xPosition = 0f,
                    labels = listOf(
                        TooltipBubbleData(labelName = "$rowLabel, $colLabel", value = cell.label ?: cell.value.toString())
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
                    onClose = { selectedCell = null }
                )
            }
        }
    }
    }
}
