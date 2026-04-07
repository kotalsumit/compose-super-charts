package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class RadarWebType {
    POLYGON,
    CIRCLE
}

data class RadarChartStyleConfig(
    val webType: RadarWebType = RadarWebType.POLYGON,
    val levels: Int = 5,
    val webLineWidth: Dp = 1.dp,
    val webLineColor: Color = Color.LightGray,
    val axisLineWidth: Dp = 1.5.dp,
    val axisLineColor: Color = Color.Gray,
    val labelTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color.LightGray,
    val tooltipLabelTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    val tooltipValueTextStyle: TextStyle = TextStyle(
        color = Color.DarkGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    ),
    val padding: Dp = 48.dp,
    val animationDuration: Int = 500,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false
)
