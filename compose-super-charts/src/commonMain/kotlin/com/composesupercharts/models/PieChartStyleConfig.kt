package com.composesupercharts.models

import androidx.compose.foundation.layout.PaddingValues
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
    val legendTextStyle: TextStyle = TextStyle(color = Color.DarkGray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color(0x15000000),
    val tooltipElevation: Dp = 6.dp,
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val backgroundColor: Color = Color.Transparent,
    val chartSize: Dp = 250.dp,
    val legendItemSpacing: Dp = 16.dp,
    val legendRowSpacing: Dp = 8.dp,
    val legendContentAlignment: LegendContentAlignment = LegendContentAlignment.CENTER,
    val legendContentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    val legendLayoutMode: LegendLayoutMode = LegendLayoutMode.FLOW_ROW,
    val showLegendWhenSingleSeries: Boolean = true,
    val legendShapeSize: Dp = 12.dp,
    val activeSliceOffsetRatio: Float = 0.1f,
    val tooltipOffsetRatio: Float = 0.7f,
    val legendShapeRadius: Float = 6f,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val animationDuration: Int = 500,
    val centerLabel: String? = null,
    val centerValue: String? = null,
    val centerLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Normal),
    val centerValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold),
    val valueFormatter: ((Float) -> String)? = null
)
