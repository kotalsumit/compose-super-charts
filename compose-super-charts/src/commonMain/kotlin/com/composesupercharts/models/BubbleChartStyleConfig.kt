package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BubbleChartStyleConfig(
    val bubbleOpacity: Float = 0.6f,
    val bubbleStrokeWidth: Dp = 1.dp,
    val maxBubbleRadius: Dp = 40.dp,
    val axisColor: Color = Color.LightGray,
    val axisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Normal),
    val gridLinesColor: Color = Color.LightGray.copy(alpha = 0.3f),
    val showGridLines: Boolean = true,
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color.LightGray,
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val chartHeight: Dp = 300.dp,
    val padding: Dp = 16.dp
)
