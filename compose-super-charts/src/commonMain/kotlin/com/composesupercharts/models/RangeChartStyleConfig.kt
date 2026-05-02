package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RangeChartStyleConfig(
    val barHeight: Dp = 16.dp,
    val rowSpacing: Dp = 18.dp,
    val chartHeight: Dp = 260.dp,
    val yAxisWidth: Dp = 80.dp,
    val axisColor: Color = Color.LightGray,
    val labelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val valueTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val showAxisLabels: Boolean = true,
    val axisStepCount: Int = 4,
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color(0x33000000),
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val animationDuration: Int = 500,
    val valueFormatter: ((Float) -> String)? = null
)
