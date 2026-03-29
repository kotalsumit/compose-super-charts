package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ColumnChartType {
    STANDARD,
    CLUSTERED,
    STACKED
}

enum class XAxisPosition {
    TOP,
    BOTTOM
}

data class ColumnChartStyleConfig(
    val type: ColumnChartType = ColumnChartType.STANDARD,
    val barWidth: Dp = 32.dp,
    val barSpacing: Dp = 16.dp,
    val clusterSpacing: Dp = 4.dp,
    val isScrollable: Boolean = false,
    val chartHeight: Dp = 200.dp,
    val xAxisHeight: Dp = 48.dp,
    val xAxisPosition: XAxisPosition = XAxisPosition.BOTTOM,
    val showLabels: Boolean = true,
    val xAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val yAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val yAxisDividerColor: Color = Color.LightGray,
    val xAxisDividerColor: Color = Color.LightGray,
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color(0x33000000),
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val legendPosition: LegendPosition = LegendPosition.TOP,
    val legendTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val legendItemSpacing: Dp = 24.dp,
    val legendBarWidth: Dp = 12.dp,
    val legendBarHeight: Dp = 12.dp,
    val legendBarRadius: Dp = 2.dp,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val xAxisLabelRotation: Float = -45f,
    val animationDuration: Int = 1000
)
