package com.composesupercharts.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.atoms.ChartText
import com.composesupercharts.models.LegendContentAlignment
import com.composesupercharts.models.LegendLayoutMode
import com.composesupercharts.models.LegendStyleConfig

data class LegendItemData(
    val label: String,
    val color: Color
)

enum class LegendShape {
    CIRCLE,
    SQUARE,
    ROUNDED_SQUARE
}

/**
 * A universal legend molecule that can be used by any chart.
 * Standardizes the layout and ensures full-width wrapping (FlowRow).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UniversalLegend(
    modifier: Modifier = Modifier,
    items: List<LegendItemData>,
    textStyle: TextStyle,
    shape: LegendShape = LegendShape.CIRCLE,
    shapeSize: Dp = 10.dp,
    itemSpacing: Dp = 16.dp,
    rowSpacing: Dp = 8.dp,
    contentAlignment: LegendContentAlignment = LegendContentAlignment.CENTER,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    layoutMode: LegendLayoutMode = LegendLayoutMode.FLOW_ROW,
    showWhenSingleSeries: Boolean = true,
    legendStyle: LegendStyleConfig? = null,
    hiddenItemIndexes: Set<Int> = emptySet(),
    onItemClick: ((Int) -> Unit)? = null
) {
    val effectiveItemSpacing = legendStyle?.itemSpacing ?: itemSpacing
    val effectiveRowSpacing = legendStyle?.rowSpacing ?: rowSpacing
    val effectiveAlignment = legendStyle?.contentAlignment ?: contentAlignment
    val effectivePadding = legendStyle?.contentPadding ?: contentPadding
    val effectiveLayoutMode = legendStyle?.layoutMode ?: layoutMode
    val effectiveShowSingleSeries = legendStyle?.showWhenSingleSeries ?: showWhenSingleSeries

    if (items.isEmpty() || (items.size == 1 && !effectiveShowSingleSeries)) return

    val legendModifier = modifier
        .fillMaxWidth()
        .padding(effectivePadding)

    when (effectiveLayoutMode) {
        LegendLayoutMode.ROW -> {
            Row(
                modifier = legendModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(effectiveItemSpacing, effectiveAlignment.toHorizontalAlignment())
            ) {
                items.forEachIndexed { index, item ->
                    UniversalLegendItem(index, item, textStyle, shape, shapeSize, hiddenItemIndexes, onItemClick)
                }
            }
        }

        LegendLayoutMode.FLOW_ROW -> {
            FlowRow(
                modifier = legendModifier,
                horizontalArrangement = Arrangement.spacedBy(effectiveItemSpacing, effectiveAlignment.toHorizontalAlignment()),
                verticalArrangement = Arrangement.spacedBy(effectiveRowSpacing)
            ) {
                items.forEachIndexed { index, item ->
                    UniversalLegendItem(index, item, textStyle, shape, shapeSize, hiddenItemIndexes, onItemClick)
                }
            }
        }

        LegendLayoutMode.COLUMN -> {
            Column(
                modifier = legendModifier,
                horizontalAlignment = effectiveAlignment.toHorizontalAlignment(),
                verticalArrangement = Arrangement.spacedBy(effectiveRowSpacing)
            ) {
                items.forEachIndexed { index, item ->
                    UniversalLegendItem(index, item, textStyle, shape, shapeSize, hiddenItemIndexes, onItemClick)
                }
            }
        }
    }
}

@Composable
private fun UniversalLegendItem(
    index: Int,
    item: LegendItemData,
    textStyle: TextStyle,
    shape: LegendShape,
    shapeSize: Dp,
    hiddenItemIndexes: Set<Int>,
    onItemClick: ((Int) -> Unit)?
) {
    val isHidden = index in hiddenItemIndexes
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = if (onItemClick != null) Modifier.clickable { onItemClick(index) } else Modifier
    ) {
        Box(
            modifier = Modifier
                .size(shapeSize)
                .clip(
                    when (shape) {
                        LegendShape.CIRCLE -> CircleShape
                        LegendShape.SQUARE -> RoundedCornerShape(0.dp)
                        LegendShape.ROUNDED_SQUARE -> RoundedCornerShape(2.dp)
                    }
                )
                .background(item.color.copy(alpha = if (isHidden) 0.28f else 1f))
        )
        Spacer(modifier = Modifier.width(8.dp))
        ChartText(
            text = item.label,
            style = textStyle.copy(color = textStyle.color.copy(alpha = if (isHidden) 0.45f else 1f))
        )
    }
}

private fun LegendContentAlignment.toHorizontalAlignment(): Alignment.Horizontal {
    return when (this) {
        LegendContentAlignment.START -> Alignment.Start
        LegendContentAlignment.CENTER -> Alignment.CenterHorizontally
        LegendContentAlignment.END -> Alignment.End
    }
}
