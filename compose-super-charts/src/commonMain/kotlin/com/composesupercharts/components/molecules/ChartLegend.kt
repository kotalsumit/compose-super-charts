package com.composesupercharts.components.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.atoms.ChartLegendShape
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.models.ChartLineConfig
import com.composesupercharts.models.ChartStyleConfig
import com.composesupercharts.models.HollowPoint
import com.composesupercharts.models.LegendPosition
import com.composesupercharts.models.SolidPoint

/**
 * A standard legend molecule for charts.
 * 
 * Displays series names along with their respective colors/styles as defined in the config.
 * Supports TOP and BOTTOM positioning via AnimatedVisibility.
 *
 * @param modifier Layout modifiers.
 * @param legendLabels Human-readable names for the chart series.
 * @param config The shared chart style configuration.
 */
@Composable
fun ChartLegend(
    modifier: Modifier = Modifier,
    legendLabels: List<String>,
    config: ChartStyleConfig,
    hiddenSeriesIndexes: Set<Int> = emptySet(),
    onLegendClick: ((Int) -> Unit)? = null
) {
    val showLegend = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { showLegend.value = true }

    AnimatedVisibility(
        visible = showLegend.value,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                slideInVertically(
                    animationSpec = tween(durationMillis = 300),
                    initialOffsetY = { -it / 4 }
                )
    ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(config.legendItemSpacing)
            ) {
                legendLabels.forEachIndexed { index, label ->
                    val lineConfig = config.lines.getOrNull(index)
                    if (lineConfig != null) {
                        LegendItem(
                            label = label,
                            lineConfig = lineConfig,
                            config = config,
                            isHidden = index in hiddenSeriesIndexes,
                            onClick = onLegendClick?.let { click -> { click(index) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    lineConfig: ChartLineConfig,
    config: ChartStyleConfig,
    isHidden: Boolean,
    onClick: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        val isHollow = lineConfig.pointStyle is HollowPoint
        
        ChartLegendShape(
            modifier = Modifier.size(width = config.legendShapeWidth, height = config.legendShapeHeight),
            color = lineConfig.lineStyle.color.copy(alpha = if (isHidden) 0.28f else 1f),
            lineStrokeWidth = 3f,
            pointRadius = 4f,
            isHollow = isHollow,
            pathEffect = lineConfig.lineStyle.pathEffect
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        ChartText(text = label, style = config.legendTextStyle.copy(color = config.legendTextStyle.color.copy(alpha = if (isHidden) 0.45f else 1f)))
    }
}
