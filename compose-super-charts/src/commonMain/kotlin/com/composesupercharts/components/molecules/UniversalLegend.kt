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
    hiddenItemIndexes: Set<Int> = emptySet(),
    onItemClick: ((Int) -> Unit)? = null
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isHidden = index in hiddenItemIndexes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = itemSpacing / 2)
                    .then(
                        if (onItemClick != null) {
                            Modifier.clickable { onItemClick(index) }
                        } else {
                            Modifier
                        }
                    )
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
    }
}
