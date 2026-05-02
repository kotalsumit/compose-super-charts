package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.RangeChartData
import com.composesupercharts.models.RangeChartStyleConfig
import com.composesupercharts.models.TooltipBubbleData
import com.composesupercharts.utils.ChartAccessibility.rangeChartDescription

@Composable
fun RangeChart(
    modifier: Modifier = Modifier,
    data: RangeChartData,
    config: RangeChartStyleConfig = RangeChartStyleConfig()
) {
    if (data.entries.isEmpty()) return

    val density = LocalDensity.current
    val animationProgress = remember { Animatable(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(config.animationDuration))
    }

    val minValue = data.entries.minOf { minOf(it.start, it.end) }
    val maxValue = data.entries.maxOf { maxOf(it.start, it.end) }
    val range = (maxValue - minValue).coerceAtLeast(0.01f)
    val rowHeight = config.barHeight + config.rowSpacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { rangeChartDescription(data) }
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(config.chartHeight)) {
            Column(modifier = Modifier.width(config.yAxisWidth)) {
                data.entries.forEach { entry ->
                    ChartText(
                        text = entry.label,
                        style = config.labelTextStyle,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.height(rowHeight).fillMaxWidth().padding(end = 8.dp)
                    )
                }
            }

            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val chartWidthPx = constraints.maxWidth.toFloat()
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight * data.entries.size)
                        .pointerInput(data) {
                            detectTapGestures { tap ->
                                val row = (tap.y / with(density) { rowHeight.toPx() }).toInt()
                                selectedIndex = row.coerceIn(0, data.entries.lastIndex)
                            }
                        }
                ) {
                    val progress = animationProgress.value
                    data.entries.forEachIndexed { index, entry ->
                        val startX = ((entry.start - minValue) / range) * size.width
                        val endX = ((entry.end - minValue) / range) * size.width
                        val left = minOf(startX, endX)
                        val right = maxOf(startX, endX)
                        val top = index * rowHeight.toPx() + config.rowSpacing.toPx() / 2
                        drawRoundRect(
                            color = entry.color,
                            topLeft = Offset(left, top),
                            size = Size((right - left) * progress, config.barHeight.toPx()),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                }

                selectedIndex?.let { index ->
                    val entry = data.entries[index]
                    val rightValue = maxOf(entry.start, entry.end)
                    val tooltipX = ((rightValue - minValue) / range) * chartWidthPx
                    val tooltipY = with(density) {
                        val baseY = rowHeight.toPx() * index
                        if (index >= data.entries.size / 2) {
                            (baseY - 88.dp.toPx()).coerceAtLeast(0f)
                        } else {
                            baseY + config.barHeight.toPx() + 4.dp.toPx()
                        }.toDp()
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = tooltipY)
                            .zIndex(10f)
                    ) {
                        TooltipBubble(
                            xPosition = tooltipX,
                            labels = entry.tooltipData ?: listOf(
                                TooltipBubbleData("Start", config.valueFormatter?.invoke(entry.start) ?: entry.start.toString()),
                                TooltipBubbleData("End", config.valueFormatter?.invoke(entry.end) ?: entry.end.toString())
                            ),
                            isFirst = index == 0,
                            isLast = index == data.entries.lastIndex,
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

        if (config.showAxisLabels) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(config.yAxisWidth))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (step in 0..config.axisStepCount.coerceAtLeast(1)) {
                        val value = minValue + (range * step / config.axisStepCount.coerceAtLeast(1))
                        ChartText(
                            text = config.valueFormatter?.invoke(value) ?: value.toInt().toString(),
                            style = config.valueTextStyle,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
