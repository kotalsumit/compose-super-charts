package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.composesupercharts.components.atoms.ChartDivider
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.components.molecules.LegendItemData
import com.composesupercharts.components.molecules.LegendShape
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.components.molecules.UniversalLegend
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.CombinedChartData
import com.composesupercharts.models.CombinedChartStyleConfig
import com.composesupercharts.models.LegendPosition
import com.composesupercharts.models.TooltipBubbleData
import com.composesupercharts.utils.ChartAccessibility.combinedChartDescription

@Composable
fun CombinedChart(
    modifier: Modifier = Modifier,
    data: CombinedChartData,
    config: CombinedChartStyleConfig = CombinedChartStyleConfig()
) {
    if (data.points.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val animationProgress = remember { Animatable(0f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(config.animationDuration))
    }

    val maxValue = data.points.maxOf { maxOf(it.columnValue, it.lineValue) }.coerceAtLeast(1f)
    val firstPoint = data.points.first()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { combinedChartDescription(data) }
    ) {
        if (config.legendPosition == LegendPosition.TOP) {
            UniversalLegend(
                items = listOf(
                    LegendItemData(config.columnLegendLabel, firstPoint.columnColor),
                    LegendItemData(config.lineLegendLabel, firstPoint.lineColor)
                ),
                textStyle = config.legendTextStyle,
                shape = LegendShape.ROUNDED_SQUARE,
                shapeSize = config.legendShapeSize,
                itemSpacing = config.legendItemSpacing,
                rowSpacing = config.legendRowSpacing,
                contentAlignment = config.legendContentAlignment,
                contentPadding = config.legendContentPadding,
                layoutMode = config.legendLayoutMode,
                showWhenSingleSeries = config.showLegendWhenSingleSeries
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(config.chartHeight)) {
            val chartWidthPx = constraints.maxWidth.toFloat()
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.chartHeight)
                    .pointerInput(data) {
                        detectTapGestures { tap ->
                            val pointWidth = size.width / data.points.size
                            selectedIndex = (tap.x / pointWidth).toInt().coerceIn(0, data.points.lastIndex)
                        }
                    }
            ) {
                val spacing = size.width / data.points.size
                val progress = animationProgress.value
                val path = Path()

                data.points.forEachIndexed { index, point ->
                    val centerX = spacing * index + spacing / 2
                    val columnHeight = (point.columnValue / maxValue) * size.height * progress
                    drawRoundRect(
                        color = point.columnColor,
                        topLeft = Offset(centerX - config.columnWidth.toPx() / 2, size.height - columnHeight),
                        size = Size(config.columnWidth.toPx(), columnHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    if (config.showValueLabels) {
                        val label = config.valueFormatter?.invoke(point.columnValue) ?: point.columnValue.toInt().toString()
                        val layout = textMeasurer.measure(label, config.valueTextStyle)
                        val labelY = (size.height - columnHeight - layout.size.height - 4.dp.toPx()).coerceAtLeast(0f)
                        drawText(layout, topLeft = Offset(centerX - layout.size.width / 2, labelY))
                    }

                    val lineY = size.height - (point.lineValue / maxValue) * size.height * progress
                    if (index == 0) path.moveTo(centerX, lineY) else path.lineTo(centerX, lineY)
                }

                drawPath(path, firstPoint.lineColor, style = Stroke(config.lineWidth))
                data.points.forEachIndexed { index, point ->
                    val centerX = spacing * index + spacing / 2
                    val lineY = size.height - (point.lineValue / maxValue) * size.height * progress
                    drawCircle(point.lineColor, config.pointRadius, Offset(centerX, lineY))
                }
            }

            selectedIndex?.let { index ->
                val point = data.points[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(10f)
                ) {
                    val chartPointX = chartWidthPx * ((index + 0.5f) / data.points.size)
                    TooltipBubble(
                        xPosition = chartPointX,
                        labels = listOf(
                            TooltipBubbleData(config.columnLegendLabel, config.valueFormatter?.invoke(point.columnValue) ?: point.columnValue.toInt().toString()),
                            TooltipBubbleData(config.lineLegendLabel, config.valueFormatter?.invoke(point.lineValue) ?: point.lineValue.toInt().toString())
                        ),
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

        ChartDivider(color = config.axisColor, thickness = 1.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            data.points.forEach { point ->
                ChartText(
                    text = point.label,
                    style = config.labelTextStyle,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (config.legendPosition == LegendPosition.BOTTOM) {
            UniversalLegend(
                items = listOf(
                    LegendItemData(config.columnLegendLabel, firstPoint.columnColor),
                    LegendItemData(config.lineLegendLabel, firstPoint.lineColor)
                ),
                textStyle = config.legendTextStyle,
                shape = LegendShape.ROUNDED_SQUARE,
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
