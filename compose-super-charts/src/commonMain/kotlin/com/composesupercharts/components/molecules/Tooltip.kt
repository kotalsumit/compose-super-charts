package com.composesupercharts.components.molecules

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.composesupercharts.components.atoms.ChartSurface
import com.composesupercharts.components.atoms.ChartText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.composesupercharts.models.ChartPointData
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.TooltipBubbleData
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * Highlight and Tooltip molecule.
 * 
 * Orchestrates the rendering of vertical highlight lines and the floating tooltip bubbles
 * when a user interacts with the chart.
 *
 * @param index The currently selected point index.
 * @param labels Data for the tooltip (titles and values).
 * @param points The full dataset to calculate anchor positions.
 * @param config Styling configuration for lines, paths, and bubbles.
 */
@Composable
fun HighlightAndTooltip(
    index: Int,
    points: List<ChartPointData>,
    config: ChartStyleConfig,
    labels: List<TooltipBubbleData>,
    onClose: () -> Unit = {}
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val spacing = widthPx / (points.size - 1).coerceAtLeast(1)
        val xPosition = spacing * index

        Canvas(modifier = Modifier.fillMaxSize()) {
            val dashColor = config.tooltipDashColor ?: config.lines.firstOrNull()?.lineStyle?.color?.copy(alpha = 0.6f) ?: Color.Gray

            drawLine(
                    color = dashColor,
                    start = Offset(xPosition, 0f),
                    end = Offset(xPosition, heightPx),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(config.tooltipDashIntervals, 0f)
            )
        }

        val isFirst = index == 0
        val isLast = index == points.size - 1

        TooltipBubble(
            xPosition = xPosition,
            labels = labels,
            isFirst = isFirst,
            isLast = isLast,
            config = config,
            onClose = onClose
        )
    }
}


@Composable
fun TooltipBubble(
    xPosition: Float,
    labels: List<TooltipBubbleData>,
    isFirst: Boolean,
    isLast: Boolean,
    config: ChartStyleConfig,
    onClose: () -> Unit = {}
) {
    LaunchedEffect(xPosition, labels) {
        config.tooltipAutoDismissMs?.let { delayTime ->
            delay(delayTime)
            onClose()
        }
    }

    Box(modifier = Modifier.zIndex(10f)) {
        Box(modifier = Modifier.offset { IntOffset(xPosition.toInt(), -10) }) {
            ChartSurface(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(placeable.width, placeable.height) {
                            val desiredShift = when {
                                isFirst -> 0
                                isLast -> -placeable.width
                                else -> -(placeable.width / 2)
                            }
                            
                            // Prevent clipping by ensuring the tooltip stays within horizontal bounds
                            // We use the constraints.maxWidth from the parent BoxWithConstraints if available,
                            // but here we are in a sub-Box. We need to know the absolute xPosition.
                            // Better approach: just use the shift to avoid negative absolute X.
                            // But TooltipBubble doesn't know its absolute X unless we pass it.
                            // Let's use the provided xPosition.
                            
                            var finalShift = desiredShift
                            if (xPosition + finalShift < 0) {
                                finalShift = -xPosition.toInt()
                            }
                            
                            // Check right edge if possible. Since we don't have parentWidth here easily, 
                            // we rely on isLast flag from the caller for now, but also 
                            // we can check if the tooltip would be huge.
                            
                            placeable.placeRelative(finalShift, 0)
                        }
                    }
                    .padding(horizontal = config.tooltipHorizontalPadding * 1.5f, vertical = config.tooltipVerticalPadding * 1.5f),
                backgroundColor = if (config.tooltipBackgroundColor == Color.Unspecified) Color.White else config.tooltipBackgroundColor.copy(alpha = 1f),
                borderColor = config.tooltipBorderColor,
                elevation = config.tooltipElevation
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = config.tooltipHorizontalPadding,
                        vertical = config.tooltipVerticalPadding
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(end = if (config.showTooltipCloseButton) 24.dp else 0.dp)
                    ) {
                        labels.forEachIndexed { index, labelData ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                labelData.labelName?.takeIf { it.isNotBlank() }?.let {
                                    ChartText(
                                        text = it,
                                        style = config.tooltipLabelTextStyle,
                                        modifier = Modifier.padding(end = config.tooltipInnerSpacing)
                                    )
                                }

                                val valStr = if (labelData.value.isNullOrBlank()) "0" else labelData.value

                                ChartText(
                                    text = valStr,
                                    style = config.tooltipValueTextStyle
                                )
                            }
                        }
                    }

                    if (config.showTooltipCloseButton) {
                        Surface(
                            color = config.tooltipBackgroundColor.copy(alpha = 0.95f),
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .zIndex(11f),
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, config.tooltipBorderColor)
                        ) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Tooltip",
                                    tint = config.tooltipLabelTextStyle.color.copy(alpha = 0.8f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
