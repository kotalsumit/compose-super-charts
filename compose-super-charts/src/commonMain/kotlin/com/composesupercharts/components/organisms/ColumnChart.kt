package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.atoms.ChartDivider
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.components.molecules.TooltipBubble
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.ColumnChartData
import com.composesupercharts.models.ColumnChartStyleConfig
import com.composesupercharts.models.ColumnChartType
import com.composesupercharts.models.LegendPosition
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

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val maxOfY = remember(data, maxY) {
        val highest = data.points.maxOf { point ->
            if (config.type == ColumnChartType.STACKED) point.values.sum() else point.values.maxOrNull() ?: 1f
        }
        val bufferedHighest = highest * 1.2f
        val step = kotlin.math.ceil(bufferedHighest / maxY).coerceAtLeast(1f)
        maxY * step
    }

    val scrollState = rememberScrollState()
    val scrollModifier = if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) Modifier.horizontalScroll(scrollState) else Modifier
    
    val totalBarWidth = when (config.type) {
        ColumnChartType.CLUSTERED -> {
            val maxBars = data.points.maxOf { it.values.size }
            (config.barWidth * maxBars) + (config.clusterSpacing * (maxBars - 1))
        }
        else -> config.barWidth
    }
    
    val itemWidth = totalBarWidth + config.barSpacing * 2
    val chartContentWidth = itemWidth * data.points.size

    Column(modifier = modifier.fillMaxWidth()) {
        if (config.legendPosition == LegendPosition.TOP && legendLabels != null) {
            ColumnChartLegend(labels = legendLabels, data = data, config = config)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(modifier = Modifier.padding(horizontal = 0.dp)) {
            Column {
                if (config.xAxisPosition == XAxisPosition.TOP) {
                    Spacer(modifier = Modifier.height(config.xAxisHeight))
                }
                Column(
                    modifier = Modifier.height(config.chartHeight).padding(end = 8.dp),
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

            BoxWithConstraints(modifier = Modifier.weight(1f).then(scrollModifier)) {
                val constraintsScope = this
                val spacing = if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) itemWidth else constraintsScope.maxWidth / data.points.size.coerceAtLeast(1)

                Column {
                    if (config.xAxisPosition == XAxisPosition.TOP) {
                        Row(modifier = Modifier.width(if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) chartContentWidth else constraintsScope.maxWidth).height(config.xAxisHeight)) {
                            data.points.forEach { point ->
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                                    ChartText(
                                        text = point.label,
                                        style = config.xAxisLabelTextStyle,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                        ChartDivider(color = config.xAxisDividerColor, thickness = 1.dp)
                    }

                    Box(
                        modifier = Modifier.width(if (config.isScrollable || config.type == ColumnChartType.CLUSTERED) chartContentWidth else constraintsScope.maxWidth)
                            .height(config.chartHeight)
                            .pointerInput(data) {
                                detectTapGestures { tap ->
                                    val pointSize = size.width / data.points.size
                                    val index = (tap.x / pointSize).toInt().coerceIn(0, data.points.lastIndex)
                                    selectedIndex = if (selectedIndex == index) null else index
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(config.chartHeight)) {
                            val canvasSpacing = size.width / data.points.size
                            val progress = animationProgress.value

                            data.points.forEachIndexed { index, point ->
                                val centerX = canvasSpacing * index + canvasSpacing / 2
                                
                                when (config.type) {
                                    ColumnChartType.STANDARD -> {
                                        val val0 = point.values.getOrNull(0) ?: 0f
                                        val barHeight = (val0 / maxOfY) * size.height * progress
                                        drawRoundRect(
                                            color = point.colors.getOrNull(0) ?: Color.Gray,
                                            topLeft = Offset(centerX - config.barWidth.toPx() / 2, size.height - barHeight),
                                            size = Size(config.barWidth.toPx(), barHeight),
                                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                        )
                                    }
                                    ColumnChartType.CLUSTERED -> {
                                        val clusterWidth = (config.barWidth.toPx() * point.values.size) + (config.clusterSpacing.toPx() * (point.values.size - 1))
                                        var currentX = centerX - clusterWidth / 2
                                        point.values.forEachIndexed { valIdx, value ->
                                            val barHeight = (value / maxOfY) * size.height * progress
                                            drawRoundRect(
                                                color = point.colors.getOrNull(valIdx) ?: Color.Gray,
                                                topLeft = Offset(currentX, size.height - barHeight),
                                                size = Size(config.barWidth.toPx(), barHeight),
                                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                            )
                                            currentX += config.barWidth.toPx() + config.clusterSpacing.toPx()
                                        }
                                    }
                                    ColumnChartType.STACKED -> {
                                        var currentY = size.height
                                        point.values.forEachIndexed { valIdx, value ->
                                            val barHeight = (value / maxOfY) * size.height * progress
                                            drawRect(
                                                color = point.colors.getOrNull(valIdx) ?: Color.Gray,
                                                topLeft = Offset(centerX - config.barWidth.toPx() / 2, currentY - barHeight),
                                                size = Size(config.barWidth.toPx(), barHeight)
                                            )
                                            currentY -= barHeight
                                        }
                                    }
                                }
                            }
                        }

                        selectedIndex?.let { index ->
                            val point = data.points[index]
                            val centerX = spacing * index + spacing / 2
                            
                            Box(modifier = Modifier.offset(x = centerX - 40.dp, y = 0.dp)) {
                                TooltipBubble(
                                    xPosition = 0f,
                                    labels = point.tooltipData ?: point.values.mapIndexed { idx, v -> TooltipBubbleData(legendLabels?.getOrNull(idx) ?: "Value", v.toInt().toString()) },
                                    isFirst = false,
                                    isLast = false,
                                    config = ChartStyleConfig(
                                        lines = emptyList(),
                                        tooltipBackgroundColor = config.tooltipBackgroundColor,
                                        tooltipBorderColor = config.tooltipBorderColor,
                                        tooltipLabelTextStyle = config.tooltipLabelTextStyle,
                                        tooltipValueTextStyle = config.tooltipValueTextStyle
                                    )
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
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (config.legendPosition == LegendPosition.BOTTOM && legendLabels != null) {
            Spacer(modifier = Modifier.height(16.dp))
            ColumnChartLegend(labels = legendLabels, data = data, config = config)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnChartLegend(labels: List<String>, data: ColumnChartData, config: ColumnChartStyleConfig) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        labels.forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = config.legendItemSpacing / 2)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(config.legendBarWidth, config.legendBarHeight)) {
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
