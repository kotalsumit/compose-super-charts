package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class VennDiagramStyleConfig(
    val circleOpacity: Float = 0.5f,
    val circleStrokeWidth: Dp = 2.dp,
    val showLabels: Boolean = true,
    val labelTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color.LightGray,
    val tooltipLabelTextStyle: TextStyle = TextStyle(
        color = Color.Gray,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal
    ),
    val tooltipValueTextStyle: TextStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    val padding: Dp = 16.dp,
    val circleRadiusScale: Float = 0.9f,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val animationDuration: Int = 500
)
