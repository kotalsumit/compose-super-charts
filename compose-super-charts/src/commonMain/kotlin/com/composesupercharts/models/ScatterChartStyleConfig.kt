package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ScatterChartStyleConfig(
    val defaultPointRadius: Dp = 6.dp,
    val pointStrokeWidth: Dp = 1.dp,
    val axisColor: Color = Color.Gray,
    val axisThickness: Dp = 2.dp,
    val gridLineColor: Color = Color.LightGray.copy(alpha = 0.5f),
    val showGridLines: Boolean = true,
    val labelTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    ),
    val xAxisLabelRotation: Float = -45f,
    val titleTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
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
    val padding: Dp = 32.dp,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val animationDuration: Int = 500
)
