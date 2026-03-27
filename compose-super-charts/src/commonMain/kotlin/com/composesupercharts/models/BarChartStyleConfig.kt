package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BarChartType {
    STANDARD,
    CLUSTERED,
    STACKED
}

enum class YAxisPosition {
    LEFT,
    RIGHT
}

data class BarChartStyleConfig(
    val type: BarChartType = BarChartType.STANDARD,
    val barThickness: Dp = 24.dp,
    val barSpacing: Dp = 12.dp,
    val clusterSpacing: Dp = 4.dp,
    val chartWidth: Dp = 300.dp,
    val chartHeight: Dp = 300.dp,
    val isScrollable: Boolean = true,
    val yAxisWidth: Dp = 80.dp,
    val yAxisPosition: YAxisPosition = YAxisPosition.LEFT,
    val showLabels: Boolean = true,
    val xAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val yAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val yAxisDividerColor: Color = Color.LightGray,
    val xAxisDividerColor: Color = Color.LightGray,
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color(0x15000000),
    val tooltipElevation: Dp = 6.dp,
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
