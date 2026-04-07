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
    val valueArcColor: Color = Color(0xFF2196F3),
    val needleColor: Color = Color(0xFF616161),
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
    val animationDuration: Int = 500,
    val startAngle: Float = 180f,
    val sweepAngle: Float = 180f,
    val padding: Dp = 16.dp,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color(0x33000000),
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
)
