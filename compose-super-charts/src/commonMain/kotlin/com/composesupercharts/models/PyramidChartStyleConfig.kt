package com.composesupercharts.models

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PyramidChartType {
    PYRAMID,
    FUNNEL
}

data class PyramidChartStyleConfig(
    val type: PyramidChartType = PyramidChartType.PYRAMID,
    val spacing: Dp = 4.dp,
    val segmentLabelTextStyle: TextStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val outsideLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Normal),
    val showLabels: Boolean = true,
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color.LightGray,
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val legendPosition: LegendPosition = LegendPosition.BOTTOM,
    val legendTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Normal),
    val legendItemSpacing: Dp = 16.dp,
    val legendRowSpacing: Dp = 8.dp,
    val legendContentAlignment: LegendContentAlignment = LegendContentAlignment.CENTER,
    val legendContentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    val legendLayoutMode: LegendLayoutMode = LegendLayoutMode.FLOW_ROW,
    val showLegendWhenSingleSeries: Boolean = true,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val animationDuration: Int = 500
)
