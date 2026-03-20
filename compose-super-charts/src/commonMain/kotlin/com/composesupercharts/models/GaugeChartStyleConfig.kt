package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GaugeChartStyleConfig(
    val arcThickness: Dp = 30.dp,
    val backgroundArcColor: Color = Color.LightGray.copy(alpha = 0.3f),
    val needleColor: Color = Color.Black,
    val needleWidth: Dp = 4.dp,
    val needleBaseRadius: Dp = 8.dp,
    val valueTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    ),
    val unitTextStyle: TextStyle = TextStyle(
        color = Color.Gray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    val rangeLabelTextStyle: TextStyle = TextStyle(
        color = Color.DarkGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal
    ),
    val animationDuration: Int = 1000,
    val startAngle: Float = 180f,
    val sweepAngle: Float = 180f,
    val padding: Dp = 16.dp
)
