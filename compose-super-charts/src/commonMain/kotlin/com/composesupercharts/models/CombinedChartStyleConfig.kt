package com.composesupercharts.models

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CombinedChartStyleConfig(
    val chartHeight: Dp = 260.dp,
    val columnWidth: Dp = 28.dp,
    val lineWidth: Float = 4f,
    val pointRadius: Float = 6f,
    val axisColor: Color = Color.LightGray,
    val labelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val valueTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val legendPosition: LegendPosition = LegendPosition.TOP,
    val legendTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val legendItemSpacing: Dp = 20.dp,
    val legendRowSpacing: Dp = 8.dp,
    val legendContentAlignment: LegendContentAlignment = LegendContentAlignment.CENTER,
    val legendContentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    val legendLayoutMode: LegendLayoutMode = LegendLayoutMode.FLOW_ROW,
    val showLegendWhenSingleSeries: Boolean = true,
    val legendShapeSize: Dp = 10.dp,
    val columnLegendLabel: String = "Column",
    val lineLegendLabel: String = "Line",
    val tooltipBackgroundColor: Color = Color.White,
    val tooltipBorderColor: Color = Color(0x33000000),
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false,
    val showValueLabels: Boolean = false,
    val animationDuration: Int = 500,
    val valueFormatter: ((Float) -> String)? = null
)
