package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CandlestickChartStyleConfig(
    val bullishColor: Color = Color(0xFF4CAF50),
    val bearishColor: Color = Color(0xFFF44336),
    val wickWidth: Dp = 1.dp,
    val bodyWidthRatio: Float = 0.8f,
    val axisColor: Color = Color.Gray,
    val axisThickness: Dp = 1.dp,
    val xAxisLabelTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    ),
    val yAxisLabelTextStyle: TextStyle = TextStyle(
        color = Color.Gray,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal
    ),
    val yAxisWidth: Dp = 60.dp,
    val xAxisLabelRotation: Float = -45f,
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
    val padding: Dp = 32.dp,
    val animationDuration: Int = 500,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val isScrollable: Boolean = false,
    val candleWidth: Dp = 16.dp
)
