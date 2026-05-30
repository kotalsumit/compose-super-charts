package com.composesupercharts.models

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class LegendContentAlignment {
    START,
    CENTER,
    END
}

enum class LegendLayoutMode {
    ROW,
    FLOW_ROW,
    COLUMN
}

enum class LegendToggleMode {
    NONE,
    HIDE_SERIES,
    DIM_SERIES
}

data class LegendStyleConfig(
    val contentAlignment: LegendContentAlignment = LegendContentAlignment.CENTER,
    val contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    val layoutMode: LegendLayoutMode = LegendLayoutMode.FLOW_ROW,
    val itemSpacing: Dp = 16.dp,
    val rowSpacing: Dp = 8.dp,
    val showWhenSingleSeries: Boolean = true
)
