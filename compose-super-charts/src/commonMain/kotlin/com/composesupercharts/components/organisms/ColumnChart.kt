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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.utils.ChartAccessibility.columnChartDescription
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.composesupercharts.utils.rotatedLayout
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.atoms.ChartDivider
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.components.molecules.UniversalLegend
import com.composesupercharts.components.molecules.LegendItemData
import com.composesupercharts.components.molecules.LegendShape
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.ColumnChartData
import com.composesupercharts.models.ColumnChartStyleConfig
import com.composesupercharts.models.ColumnChartType
import com.composesupercharts.models.LegendPosition
import com.composesupercharts.models.LegendToggleMode
import com.composesupercharts.models.TooltipBubbleData
import com.composesupercharts.models.XAxisPosition

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnChart(
    modifier: Modifier = Modifier,
    data: ColumnChartData,
    maxY: Int,
    legendLabels: List<String>? = null,
    config: ColumnChartStyleConfig = ColumnChartStyleConfig()
) {
    if (data.points.isEmpty()) return

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
    var hiddenSeriesIndexes by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val legendToggleMode = when {
        config.legendToggleMode != LegendToggleMode.NONE -> config.legendToggleMode
        config.allowLegendToggle -> LegendToggleMode.HIDE_SERIES
        else -> LegendToggleMode.NONE
    }
    val hiddenColumnIndexes = if (legendToggleMode == LegendToggleMode.HIDE_SERIES) hiddenSeriesIndexes else emptySet()
    val dimmedColumnIndexes = if (legendToggleMode == LegendToggleMode.DIM_SERIES) hiddenSeriesIndexes else emptySet()
    val maxOfY = remember(data, maxY, config.type) {
        val highest = data.points.maxOf { point ->
            if (config.type == ColumnChartType.STACKED) point.values.sum() else point.values.maxOrNull() ?: 1f
        }
        val bufferedHighest = highest * 1.2f
        val step = kotlin.math.ceil(bufferedHighest / maxY).coerceAtLeast(1f)
        maxY * step
    }

    val scrollState = rememberScrollState()
    val scrollModifier = if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) Modifier.horizontalScroll(scrollState) else Modifier
    
    var scaleX by remember { mutableStateOf(1f) }
    
    val totalBarWidth = when (config.type) {
        ColumnChartType.CLUSTERED -> {
            val maxBars = data.points.maxOf { it.values.size }
            (config.barWidth * maxBars) + (config.clusterSpacing * (maxBars - 1))
        }
        else -> config.barWidth
    }
    
    val itemWidth = (totalBarWidth + config.barSpacing * 2) * scaleX
    val chartContentWidth = itemWidth * data.points.size

    Column(
        modifier = modifier.fillMaxWidth().semantics {
            columnChartDescription(
                seriesCount = if (data.points.isNotEmpty()) data.points[0].values.size else 0,
                categoriesCount = data.points.size
            )
        }
    ) {
        if (config.legendPosition == LegendPosition.TOP && legendLabels != null) {
            UniversalLegend(
                items = legendLabels.mapIndexed { index, label ->
                    LegendItemData(
                        label = label,
                        color = data.points.firstOrNull { it.colors.size > index }?.colors?.get(index) ?: Color.Gray
                    )
                },
                textStyle = config.legendTextStyle,
                shape = LegendShape.ROUNDED_SQUARE,
                shapeSize = config.legendBarWidth, // using existing config for backward compatibility
                itemSpacing = config.legendItemSpacing,
                rowSpacing = config.legendRowSpacing,
                contentAlignment = config.legendContentAlignment,
                contentPadding = config.legendContentPadding,
                layoutMode = config.legendLayoutMode,
                showWhenSingleSeries = config.showLegendWhenSingleSeries,
                hiddenItemIndexes = hiddenSeriesIndexes,
                onItemClick = if (legendToggleMode != LegendToggleMode.NONE) { index ->
                    hiddenSeriesIndexes = if (index in hiddenSeriesIndexes) hiddenSeriesIndexes - index else hiddenSeriesIndexes + index
                } else null
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Row(modifier = Modifier.padding(horizontal = 0.dp).height(config.chartHeight)) {
            Column(modifier = Modifier.fillMaxHeight()) {
                if (config.xAxisPosition == XAxisPosition.TOP) {
                    Spacer(modifier = Modifier.height(config.xAxisHeight))
                }
                Column(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    for (i in maxY downTo 0) {
                        ChartText(
                            text = (i * (maxOfY / maxY)).toInt().toString(),
                            style = config.yAxisLabelTextStyle,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                if (config.xAxisPosition == XAxisPosition.BOTTOM) {
                    Spacer(modifier = Modifier.height(config.xAxisHeight))
                }
            }

            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxHeight().then(scrollModifier)) {
                val constraintsScope = this
                val spacing = if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) itemWidth else constraintsScope.maxWidth / data.points.size.coerceAtLeast(1)

                Column(modifier = Modifier.fillMaxHeight()) {
                    if (config.xAxisPosition == XAxisPosition.TOP) {
                        Row(modifier = Modifier.width(if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) chartContentWidth else constraintsScope.maxWidth).height(config.xAxisHeight)) {
                            data.points.forEach { point ->
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                                    ChartText(
                                        text = point.label,
                                        style = config.xAxisLabelTextStyle,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        modifier = Modifier.padding(bottom = 8.dp).rotatedLayout(config.xAxisLabelRotation)
                                    )
                                }
                            }
                        }
                        ChartDivider(color = config.xAxisDividerColor, thickness = 1.dp)
                    }

                    Box(
                        modifier = Modifier.width(if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) chartContentWidth else constraintsScope.maxWidth)
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
                                    val pointSize = size.width / data.points.size
                                    val index = (tap.x / pointSize).toInt().coerceIn(0, data.points.lastIndex)
                                    selectedIndex = if (selectedIndex == index) null else index
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                            val canvasSpacing = size.width / data.points.size
                            val progress = animationProgress.value

                            data.points.forEachIndexed { index, point ->
                                val centerX = canvasSpacing * index + canvasSpacing / 2
                                
                                when (config.type) {
                                    ColumnChartType.STANDARD -> {
                                        if (0 in hiddenColumnIndexes) return@forEachIndexed
                                        val val0 = point.values.getOrNull(0) ?: 0f
                                        val barHeight = (val0 / maxOfY) * size.height * progress
                                        drawRoundRect(
                                            color = (point.colors.getOrNull(0) ?: Color.Gray).copy(alpha = if (0 in dimmedColumnIndexes) 0.28f else 1f),
                                            topLeft = Offset(centerX - config.barWidth.toPx() / 2, size.height - barHeight),
                                            size = Size(config.barWidth.toPx(), barHeight),
                                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                        )
                                        if (config.showValueLabels) {
                                            val label = config.valueFormatter?.invoke(val0) ?: val0.toInt().toString()
                                            val layout = textMeasurer.measure(label, config.yAxisLabelTextStyle)
                                            drawText(layout, topLeft = Offset(centerX - layout.size.width / 2, size.height - barHeight - layout.size.height - 4.dp.toPx()))
                                        }
                                    }
                                    ColumnChartType.CLUSTERED -> {
                                        val clusterWidth = (config.barWidth.toPx() * point.values.size) + (config.clusterSpacing.toPx() * (point.values.size - 1))
                                        var currentX = centerX - clusterWidth / 2
                                        point.values.forEachIndexed valueLoop@{ valIdx, value ->
                                            if (valIdx in hiddenColumnIndexes) {
                                                currentX += config.barWidth.toPx() + config.clusterSpacing.toPx()
                                                return@valueLoop
                                            }
                                            val barHeight = (value / maxOfY) * size.height * progress
                                            drawRoundRect(
                                                color = (point.colors.getOrNull(valIdx) ?: Color.Gray).copy(alpha = if (valIdx in dimmedColumnIndexes) 0.28f else 1f),
                                                topLeft = Offset(currentX, size.height - barHeight),
                                                size = Size(config.barWidth.toPx(), barHeight),
                                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                            )
                                            if (config.showValueLabels) {
                                                val label = config.valueFormatter?.invoke(value) ?: value.toInt().toString()
                                                val layout = textMeasurer.measure(label, config.yAxisLabelTextStyle)
                                                drawText(layout, topLeft = Offset(currentX + config.barWidth.toPx() / 2 - layout.size.width / 2, size.height - barHeight - layout.size.height - 4.dp.toPx()))
                                            }
                                            currentX += config.barWidth.toPx() + config.clusterSpacing.toPx()
                                        }
                                    }
                                    ColumnChartType.STACKED -> {
                                        var currentY = size.height
                                        point.values.forEachIndexed valueLoop@{ valIdx, value ->
                                            if (valIdx in hiddenColumnIndexes) return@valueLoop
                                            val barHeight = (value / maxOfY) * size.height * progress
                                            drawRect(
                                                color = (point.colors.getOrNull(valIdx) ?: Color.Gray).copy(alpha = if (valIdx in dimmedColumnIndexes) 0.28f else 1f),
                                                topLeft = Offset(centerX - config.barWidth.toPx() / 2, currentY - barHeight),
                                                size = Size(config.barWidth.toPx(), barHeight)
                                            )
                                            if (config.showValueLabels && barHeight > 18.dp.toPx()) {
                                                val label = config.valueFormatter?.invoke(value) ?: value.toInt().toString()
                                                val layout = textMeasurer.measure(label, config.yAxisLabelTextStyle)
                                                drawText(layout, topLeft = Offset(centerX - layout.size.width / 2, currentY - barHeight / 2 - layout.size.height / 2))
                                            }
                                            currentY -= barHeight
                                        }
                                    }
                                }
                            }
                        }

                        selectedIndex?.let { index ->
                            val point = data.points[index]
                            val centerX = spacing * index + spacing / 2
                            
                            Box(modifier = Modifier.fillMaxWidth()) {
                                TooltipBubble(
                                    xPosition = with(density) { centerX.toPx() },
                                    labels = point.tooltipData ?: point.values.mapIndexedNotNull { idx, v ->
                                        if (idx in hiddenColumnIndexes) null else TooltipBubbleData(legendLabels?.getOrNull(idx) ?: "Value", config.valueFormatter?.invoke(v) ?: v.toInt().toString())
                                    },
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

                    if (config.xAxisPosition == XAxisPosition.BOTTOM) {
                        ChartDivider(color = config.xAxisDividerColor, thickness = 1.dp)

                        Row(modifier = Modifier.width(if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) chartContentWidth else constraintsScope.maxWidth).height(config.xAxisHeight)) {
                            data.points.forEach { point ->
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                                    ChartText(
                                        text = point.label,
                                        style = config.xAxisLabelTextStyle,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        modifier = Modifier.padding(top = 8.dp).rotatedLayout(config.xAxisLabelRotation)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (config.legendPosition == LegendPosition.BOTTOM && legendLabels != null) {
            Spacer(modifier = Modifier.height(24.dp))
            UniversalLegend(
                items = legendLabels.mapIndexed { index, label ->
                    LegendItemData(
                        label = label,
                        color = data.points.firstOrNull { it.colors.size > index }?.colors?.get(index) ?: Color.Gray
                    )
                },
                textStyle = config.legendTextStyle,
                shape = LegendShape.ROUNDED_SQUARE,
                shapeSize = config.legendBarWidth,
                itemSpacing = config.legendItemSpacing,
                rowSpacing = config.legendRowSpacing,
                contentAlignment = config.legendContentAlignment,
                contentPadding = config.legendContentPadding,
                layoutMode = config.legendLayoutMode,
                showWhenSingleSeries = config.showLegendWhenSingleSeries,
                hiddenItemIndexes = hiddenSeriesIndexes,
                onItemClick = if (legendToggleMode != LegendToggleMode.NONE) { index ->
                    hiddenSeriesIndexes = if (index in hiddenSeriesIndexes) hiddenSeriesIndexes - index else hiddenSeriesIndexes + index
                } else null
            )
        }
    }
}
