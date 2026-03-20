package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HeatmapChartStyleConfig(
    val cellSpacing: Dp = 2.dp,
    val cornerRadius: Dp = 4.dp,
    val startColor: Color = Color(0xFFE0E0E0),
    val endColor: Color = Color(0xFFEF5350),
    val emptyCellColor: Color = Color(0xFFF5F5F5),
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
    val padding: Dp = 16.dp,
    val animationDuration: Int = 1000
)
