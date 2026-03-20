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
    val labelTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    ),
    val tooltipBackgroundColor: Color = Color.White.copy(alpha = 0.9f),
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
    val animationDuration: Int = 1000
)
