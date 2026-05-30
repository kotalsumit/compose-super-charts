package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.utils.ChartAccessibility.lineChartDescription
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.composesupercharts.components.atoms.ChartDivider
import com.composesupercharts.components.atoms.ChartText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import com.composesupercharts.utils.rotatedLayout
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.molecules.ChartLegend
import com.composesupercharts.components.molecules.HighlightAndTooltip
import com.composesupercharts.models.ChartPointData
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.AreaFillBehavior
import com.composesupercharts.models.LegendPosition
import com.composesupercharts.models.LegendToggleMode
import com.composesupercharts.models.HollowPoint
import com.composesupercharts.models.NullPointBehavior
import com.composesupercharts.models.SolidPoint
import com.composesupercharts.models.UnitType
import com.composesupercharts.models.TooltipBubbleData
import com.composesupercharts.domain.ChartMathCalculations
import com.composesupercharts.utils.formatWithUnit
import com.composesupercharts.utils.vertical

import kotlin.math.roundToInt

/**
 * A highly customizable Line Chart organism.
 * 
 * Assembler for atoms and molecules to form a complete, interactive line chart.
 * Supports multiple series, animations, and cross-platform interactions.
 *
 * @param modifier Scoping or layout modifications.
 * @param points The data points to render. Supports multi-series via List<Float>.
 * @param maxY The maximum value on the Y-axis. Used for scaling calculations.
 * @param yAxisLabel Optional label for the Y-axis (e.g., "Currency", "Temperature").
 * @param legendLabels Optional titles for the series. Required if legends are displayed.
 * @param config Comprehensive styling configuration.
 */
@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    points: List<ChartPointData>,
    maxY: Int,
    yAxisLabel: String?,
    legendLabels: List<String>? = null,
    config: ChartStyleConfig
) {
    if (points.isEmpty() || points.size < 2) {
        return
    }

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(points) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = config.animationDuration, easing = FastOutSlowInEasing)
        )
    }

    val (maxOfY, stepValue) = remember(points, maxY) {
        ChartMathCalculations.calculateYAxisMetrics(points, maxY)
    }

    val unitType = remember(maxOfY) { UnitType.fromValue(maxOfY) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    val chartHeight = config.chartHeight
    val bottomAxisHeight = config.bottomAxisHeight

    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }
    var hiddenSeriesIndexes by remember(config.lines.size) { mutableStateOf<Set<Int>>(emptySet()) }
    val legendToggleMode = when {
        config.legendToggleMode != LegendToggleMode.NONE -> config.legendToggleMode
        config.allowLegendToggle -> LegendToggleMode.HIDE_SERIES
        else -> LegendToggleMode.NONE
    }
    val hiddenLineIndexes = if (legendToggleMode == LegendToggleMode.HIDE_SERIES) hiddenSeriesIndexes else emptySet()
    val dimmedLineIndexes = if (legendToggleMode == LegendToggleMode.DIM_SERIES) hiddenSeriesIndexes else emptySet()
    val visibleLineIndexes = config.lines.indices.filter { config.lines[it].isVisible && it !in hiddenLineIndexes }
    
    // Zoom & Pan states
    var scaleX by remember { mutableStateOf(1f) }

    val scrollState = rememberScrollState()
    
    // Map of animated values to support morphing
    val animatedPoints = remember(points) {
        points.map { point ->
            point.yValues.map { value -> value?.let { Animatable(it) } }
        }
    }
    
    LaunchedEffect(points) {
        animatedPoints.forEachIndexed { pointIdx, anims ->
            anims.forEachIndexed { seriesIdx, animatable ->
                val target = points[pointIdx].yValues.getOrNull(seriesIdx)
                if (animatable != null && target != null) {
                    animatable.animateTo(
                        targetValue = target,
                        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    val scrollModifier = if (config.isScrollable) Modifier.horizontalScroll(scrollState) else Modifier
    
    // Adjusted width to account for scaleX
    val basePointSpacing = 80.dp
    val scaledPointSpacing = basePointSpacing * scaleX
    val chartWidthModifier = if (config.isScrollable) {
        Modifier.requiredWidth((points.size * scaledPointSpacing.value).dp)
    } else {
        Modifier.fillMaxWidth()
    }

    Column(
        modifier = modifier.semantics {
            lineChartDescription(
                seriesCount = config.lines.size,
                points = points,
                yAxisLabel = yAxisLabel
            )
        }
    ) {
        if (config.legendPosition == LegendPosition.TOP && legendLabels != null) {
            ChartLegend(
                legendLabels = legendLabels,
                config = config,
                hiddenSeriesIndexes = hiddenSeriesIndexes,
                onLegendClick = if (legendToggleMode != LegendToggleMode.NONE) { index ->
                    hiddenSeriesIndexes = if (index in hiddenSeriesIndexes) {
                        hiddenSeriesIndexes - index
                    } else {
                        hiddenSeriesIndexes + index
                    }
                } else null
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(modifier = Modifier.padding(horizontal = 0.dp)) {
        Row {
            yAxisLabel?.let {
                ChartText(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = config.yAxisLabelColor,
                    modifier = Modifier.vertical()
                        .rotate(-90f)
                        .padding(0.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Column(
                modifier = Modifier.wrapContentWidth()
                    .height(chartHeight)
            ) {
                Column(
                    modifier = Modifier.height(chartHeight),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in maxY downTo 1) {
                        val value = i * stepValue
                        val label = config.yAxisTickFormatter?.invoke(value)
                            ?: config.valueFormatter?.invoke(value)
                            ?: value.formatWithUnit(unitType)
                        ChartText(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = config.yAxisLabelColor,
                            modifier = Modifier.padding(start = config.yAxisLabelPadding)
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
            }
        }

        Column(modifier = Modifier.padding(start = 8.dp, end = 4.dp).weight(1f).then(scrollModifier)) {
            Box(
                modifier = chartWidthModifier
                    .height(chartHeight)
                    .pointerInput(points) {
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
                    .pointerInput(points) {
                        if (config.isClickable) {
                            detectTapGestures { tap ->
                                val spacing = size.width / (points.size - 1)
                                val index = (tap.x / spacing).roundToInt().coerceIn(0, points.lastIndex)
                                if (selectedIndex != index) {
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    selectedIndex = index
                                }
                            }
                        }
                    }
            ) {
                Spacer(
                    modifier = Modifier.fillMaxWidth().height(chartHeight).drawWithCache {
                        if (points.size < 2) return@drawWithCache onDrawBehind {}

                        val progress = animationProgress.value

                        val linePaths = config.lines.mapIndexed { lineIndex, lineConfig ->
                            // Use animated values for morphing
                            val currentPoints = points.mapIndexed { pIdx, pData ->
                                pData.copy(yValues = pData.yValues.mapIndexed { sIdx, rawValue ->
                                    when {
                                        rawValue == null && config.nullPointBehavior == NullPointBehavior.TreatAsZero -> 0f
                                        rawValue == null -> null
                                        else -> animatedPoints[pIdx].getOrNull(sIdx)?.value ?: rawValue
                                    }
                                })
                            }
                            
                            val offsets = ChartMathCalculations.generatePointOffsets(
                                points = currentPoints,
                                size = size,
                                calculatedMaxY = maxOfY,
                                nullPointBehavior = config.nullPointBehavior
                            ) { it.yValues.getOrNull(lineIndex) }
                            
                            val animatedPaths = ChartMathCalculations
                                .generateLinePaths(offsets, config.nullPointBehavior)
                                .map { rawPath ->
                                    val measure = PathMeasure().apply { setPath(rawPath, false) }
                                    Path().also { animatedPath ->
                                        measure.getSegment(0f, measure.length * progress, animatedPath, true)
                                    }
                                }
                            
                            val fillPaths = if (lineConfig.fillGradientColors != null) {
                                if (config.areaFillBehavior == AreaFillBehavior.CloseToBaselinePerSegment) {
                                    ChartMathCalculations.generateFilledPathsPerSegment(
                                        offsets = offsets,
                                        height = size.height,
                                        nullPointBehavior = config.nullPointBehavior
                                    )
                                } else {
                                    listOf(
                                        ChartMathCalculations.generateFilledPath(
                                            offsets = offsets,
                                            height = size.height,
                                            nullPointBehavior = config.nullPointBehavior
                                        )
                                    )
                                }
                            } else null

                            object { 
                                val offsets = offsets
                                val animatedPaths = animatedPaths
                                val fillPaths = fillPaths
                            }
                        }

                        val pointScale = progress.coerceIn(0.85f, 1f).let { (it - 0.85f) / 0.15f }.coerceIn(0f, 1f)

                        onDrawBehind {
                            config.lines.forEachIndexed { lineIndex, lineConfig ->
                                if (lineIndex !in visibleLineIndexes) return@forEachIndexed
                                val paths = linePaths[lineIndex]
                                
                                if (paths.fillPaths != null && lineConfig.fillGradientColors != null) {
                                    val gradientBrush = Brush.verticalGradient(
                                        colors = lineConfig.fillGradientColors.map { color ->
                                            color.copy(alpha = color.alpha * if (lineIndex in dimmedLineIndexes) 0.28f else 1f)
                                        },
                                        startY = 0f,
                                        endY = size.height
                                    )
                                    paths.fillPaths.forEach { fillPath ->
                                        drawPath(path = fillPath, brush = gradientBrush, style = Fill)
                                    }
                                }

                                paths.animatedPaths.forEach { animatedPath ->
                                    drawPath(
                                        path = animatedPath,
                                        color = lineConfig.lineStyle.color.copy(alpha = lineConfig.lineStyle.alpha * if (lineIndex in dimmedLineIndexes) 0.28f else 1f),
                                        style = Stroke(
                                            width = lineConfig.lineStyle.width,
                                            pathEffect = lineConfig.lineStyle.pathEffect,
                                            cap = lineConfig.lineStyle.cap,
                                            join = lineConfig.lineStyle.join
                                        )
                                    )
                                }

                                paths.offsets.forEach { offset ->
                                    if (offset != null) {
                                        val pStyle = lineConfig.pointStyle
                                        if (pStyle is SolidPoint) {
                                            drawCircle(
                                                color = lineConfig.lineStyle.color.copy(alpha = lineConfig.lineStyle.alpha * if (lineIndex in dimmedLineIndexes) 0.28f else 1f),
                                                radius = pStyle.radius * pointScale,
                                                center = offset
                                            )
                                        } else if (pStyle is HollowPoint) {
                                            drawCircle(
                                                color = lineConfig.lineStyle.color.copy(alpha = lineConfig.lineStyle.alpha * if (lineIndex in dimmedLineIndexes) 0.28f else 1f),
                                                radius = pStyle.radius * pointScale,
                                                center = offset,
                                                style = Stroke(width = pStyle.strokeWidth)
                                            )
                                            drawCircle(
                                                color = Color.White,
                                                radius = pStyle.innerRadius * pointScale,
                                                center = offset,
                                                style = Fill
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )

                if (config.isClickable) {
                    selectedIndex?.let { index ->
                        if (index in points.indices) {
                            val labels = buildTooltipLabels(
                                point = points[index],
                                visibleLineIndexes = visibleLineIndexes,
                                legendLabels = legendLabels,
                                config = config
                            )
                            if (labels.isNotEmpty()) {
                                HighlightAndTooltip(
                                    index = index,
                                    labels = labels,
                                    points = points,
                                    config = config,
                                    onClose = { selectedIndex = null }
                                )
                            }
                        }
                    }
                }
            }

            ChartDivider(
                color = config.xAxisDividerColor,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = chartWidthModifier.height(bottomAxisHeight),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEachIndexed { labelIndex, point ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        ChartText(
                            text = config.xAxisLabelFormatter?.invoke(point.xLabel, labelIndex) ?: point.xLabel,
                            style = config.xAxisLabelTextStyle,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.requiredWidth(80.dp).rotatedLayout(config.xAxisLabelRotation)
                        )
                    }
                }
            }
        }
        }

        if (config.legendPosition == LegendPosition.BOTTOM && legendLabels != null) {
            Spacer(modifier = Modifier.height(16.dp))
            ChartLegend(
                legendLabels = legendLabels,
                config = config,
                hiddenSeriesIndexes = hiddenSeriesIndexes,
                onLegendClick = if (legendToggleMode != LegendToggleMode.NONE) { index ->
                    hiddenSeriesIndexes = if (index in hiddenSeriesIndexes) {
                        hiddenSeriesIndexes - index
                    } else {
                        hiddenSeriesIndexes + index
                    }
                } else null
            )
        }
    }
}

private fun buildTooltipLabels(
    point: ChartPointData,
    visibleLineIndexes: List<Int>,
    legendLabels: List<String>?,
    config: ChartStyleConfig
): List<TooltipBubbleData> {
    return visibleLineIndexes.mapNotNull { seriesIndex ->
        val rawValue = point.yValues.getOrNull(seriesIndex)
        val value = rawValue ?: if (config.nullPointBehavior == NullPointBehavior.TreatAsZero) 0f else null
        if (value == null) {
            null
        } else {
            val providedLabel = point.highlightLabels.getOrNull(seriesIndex)
            TooltipBubbleData(
                labelName = providedLabel?.labelName ?: legendLabels?.getOrNull(seriesIndex) ?: "Series ${seriesIndex + 1}",
                value = providedLabel?.value
                    ?: config.tooltipValueFormatter?.invoke(value)
                    ?: config.valueFormatter?.invoke(value)
                    ?: value.toString()
            )
        }
    }
}
