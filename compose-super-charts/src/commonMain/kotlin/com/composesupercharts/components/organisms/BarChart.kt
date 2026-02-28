package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import com.composesupercharts.utils.ChartAccessibility.barChartDescription
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.atoms.ChartDivider
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: BarChartData,
    maxX: Int,
    legendLabels: List<String>? = null,
    config: BarChartStyleConfig = BarChartStyleConfig()
) {
    if (data.points.isEmpty()) return
    val density = LocalDensity.current

    val animationProgress = remember { Animatable(0f) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(data) {
        selectedIndex = null
    }

    val maxOfX = remember(data, maxX, config.type) {
        val highest = data.points.maxOf { point ->
            if (config.type == BarChartType.STACKED) point.values.sum() else point.values.maxOrNull() ?: 1f
        }
        val bufferedHighest = highest * 1.2f
        val step = kotlin.math.ceil(bufferedHighest / maxX).coerceAtLeast(1f)
        maxX * step
    }

    val totalPointThickness = when (config.type) {
        BarChartType.CLUSTERED -> {
            val maxBars = data.points.maxOf { it.values.size }
            (config.barThickness * maxBars) + (config.clusterSpacing * (maxBars - 1))
        }
        else -> config.barThickness
    }
    
    val itemThickness = totalPointThickness + config.barSpacing * 2
    val chartContentHeight = itemThickness * data.points.size

    val animatedValues = remember(data) {
        data.points.map { point ->
            point.values.map { v -> Animatable(0f) }
        }
    }

    LaunchedEffect(data) {
        animatedValues.forEachIndexed { pIdx, anims ->
            anims.forEachIndexed { vIdx, animatable ->
                animatable.animateTo(
                    targetValue = data.points[pIdx].values.getOrNull(vIdx) ?: 0f,
                    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth().semantics {
            barChartDescription(
                seriesCount = if (data.points.isNotEmpty()) data.points[0].values.size else 0,
                points = emptyList(),
                yAxisLabel = null
            )
        }
    ) {
        if (config.legendPosition == LegendPosition.TOP && legendLabels != null) {
            BarChartLegend(labels = legendLabels, data = data, config = config)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            if (config.yAxisPosition == YAxisPosition.LEFT) {
                YAxisLabels(data, config, chartContentHeight)
                ChartDivider(color = config.yAxisDividerColor, thickness = 1.dp, modifier = Modifier.height(chartContentHeight).width(1.dp))
            }

            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                Column {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(chartContentHeight)
                            .padding(horizontal = 16.dp)
                            .pointerInput(data) {
                                detectTapGestures { tap ->
                                    tapOffset = tap
                                    val pointThickness = size.height / data.points.size
                                    val index = (tap.y / pointThickness).toInt().coerceIn(0, data.points.lastIndex)
                                    if (selectedIndex != index) {
                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    }
                                    selectedIndex = if (selectedIndex == index) null else index
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasItemThickness = size.height / data.points.size
                            val progress = animationProgress.value
                            val cornerRadiusPx = 4.dp.toPx()

                            data.points.forEachIndexed { index, point ->
                                val centerY = canvasItemThickness * index + canvasItemThickness / 2
                                
                                when (config.type) {
                                    BarChartType.STANDARD -> {
                                        val val0 = animatedValues[index].getOrNull(0)?.value ?: 0f
                                        val barWidth = (val0 / maxOfX) * size.width
                                        drawRoundRect(
                                            color = point.colors.getOrNull(0) ?: Color.Gray,
                                            topLeft = Offset(0f, centerY - config.barThickness.toPx() / 2),
                                            size = Size(barWidth, config.barThickness.toPx()),
                                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                                        )
                                    }
                                    BarChartType.CLUSTERED -> {
                                        val barThicknessPx = config.barThickness.toPx()
                                        val clusterSpacingPx = config.clusterSpacing.toPx()
                                        val clusterThickness = (barThicknessPx * point.values.size) + (clusterSpacingPx * (point.values.size - 1))
                                        var currentY = centerY - clusterThickness / 2
                                        point.values.forEachIndexed { valIdx, _ ->
                                            val value = animatedValues[index].getOrNull(valIdx)?.value ?: 0f
                                            val barWidth = (value / maxOfX) * size.width
                                            drawRoundRect(
                                                color = point.colors.getOrNull(valIdx) ?: Color.Gray,
                                                topLeft = Offset(0f, currentY),
                                                size = Size(barWidth, barThicknessPx),
                                                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                                            )
                                            currentY += barThicknessPx + clusterSpacingPx
                                        }
                                    }
                                    BarChartType.STACKED -> {
                                        val barThicknessPx = config.barThickness.toPx()
                                        var currentX = 0f
                                        point.values.forEachIndexed { valIdx, _ ->
                                            val value = animatedValues[index].getOrNull(valIdx)?.value ?: 0f
                                            val barWidth = (value / maxOfX) * size.width
                                            drawRect(
                                                color = point.colors.getOrNull(valIdx) ?: Color.Gray,
                                                topLeft = Offset(currentX, centerY - barThicknessPx / 2),
                                                size = Size(barWidth, barThicknessPx)
                                            )
                                            currentX += barWidth
                                        }
                                    }
                                }
                            }
                        }

                        selectedIndex?.let { index ->
                            val point = data.points.getOrNull(index) ?: return@let
                            val itemThicknessPx = with(density) { itemThickness.toPx() }
                            val centerY = itemThicknessPx * index + (itemThicknessPx / 2)
                            
                            Box(modifier = Modifier.offset(
                                x = with(density) { (tapOffset.x).toDp() },
                                y = with(density) { (centerY).toDp() - 40.dp }
                            )) {
                                TooltipBubble(
                                    xPosition = 0f,
                                    labels = point.tooltipData ?: point.values.mapIndexed { idx, v -> TooltipBubbleData(legendLabels?.getOrNull(idx) ?: "Value", v.toInt().toString()) },
                                    isFirst = false,
                                    isLast = false,
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

                    ChartDivider(color = config.xAxisDividerColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0..maxX) {
                            ChartText(
                                text = (i * (maxOfX / maxX)).toInt().toString(),
                                style = config.xAxisLabelTextStyle,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            if (config.yAxisPosition == YAxisPosition.RIGHT) {
                ChartDivider(color = config.yAxisDividerColor, thickness = 1.dp, modifier = Modifier.height(chartContentHeight).width(1.dp))
                YAxisLabels(data, config, chartContentHeight)
            }
        }

        if (config.legendPosition == LegendPosition.BOTTOM && legendLabels != null) {
            Spacer(modifier = Modifier.height(16.dp))
            BarChartLegend(labels = legendLabels, data = data, config = config)
        }
    }
}

@Composable
private fun YAxisLabels(data: BarChartData, config: BarChartStyleConfig, height: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier.width(config.yAxisWidth).height(height),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        data.points.forEach { point ->
            ChartText(
                text = point.label,
                style = config.yAxisLabelTextStyle,
                textAlign = if (config.yAxisPosition == YAxisPosition.LEFT) TextAlign.End else TextAlign.Start,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BarChartLegend(labels: List<String>, data: BarChartData, config: BarChartStyleConfig) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = config.legendItemSpacing / 2)
            ) {
                Canvas(modifier = Modifier.size(config.legendBarWidth, config.legendBarHeight)) {
                    drawRoundRect(
                        color = data.points.firstOrNull()?.colors?.getOrNull(index) ?: Color.Gray,
                        cornerRadius = CornerRadius(config.legendBarRadius.toPx(), config.legendBarRadius.toPx())
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
                ChartText(text = label, style = config.legendTextStyle)
            }
        }
    }
}
