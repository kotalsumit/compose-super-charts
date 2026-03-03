package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PieChartStyleConfig(
    val innerRadiusRatio: Float = 0f,
    val sliceSpacing: Float = 0f,
    val startAngle: Float = -90f,
    val showLabels: Boolean = true,
    val labelTextStyle: TextStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold),
    val legendPosition: LegendPosition = LegendPosition.BOTTOM,
    val legendTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipBackgroundColor: Color = Color(0xFAFFFFFF),
    val tooltipBorderColor: Color = Color(0x15000000),
    val tooltipElevation: Dp = 6.dp,
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val backgroundColor: Color = Color.Transparent,
    val chartSize: Dp = 250.dp,
    val legendItemSpacing: Dp = 16.dp,
    val legendShapeSize: Dp = 12.dp,
    val activeSliceOffsetRatio: Float = 0.1f,
    val tooltipOffsetRatio: Float = 0.7f,
    val legendShapeRadius: Float = 6f,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false
)
