package com.composesupercharts.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

interface LineStyle {
    val color: Color
    val width: Float
    val pathEffect: PathEffect?
}

data class SolidLine(
    override val color: Color,
    override val width: Float = 4f
) : LineStyle {
    override val pathEffect: PathEffect? = null
}

data class DashedLine(
    override val color: Color,
    override val width: Float = 3f,
    val intervals: FloatArray = floatArrayOf(10f, 10f)
) : LineStyle {
    override val pathEffect: PathEffect = PathEffect.dashPathEffect(intervals)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DashedLine) return false

        if (color != other.color) return false
        if (width != other.width) return false
        if (!intervals.contentEquals(other.intervals)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + intervals.contentHashCode()
        return result
    }
}

interface PointStyle {
    val radius: Float
    val innerRadius: Float?
    val strokeWidth: Float? 
}

data class SolidPoint(
    override val radius: Float = 8f
) : PointStyle {
    override val innerRadius: Float? = null
    override val strokeWidth: Float? = null
}

data class HollowPoint(
    override val radius: Float = 8f,
    override val innerRadius: Float = 6f,
    override val strokeWidth: Float = 2f
) : PointStyle

data class ChartLineConfig(
    val lineStyle: LineStyle,
    val pointStyle: PointStyle,
    val fillGradientColors: List<Color>? = null
)

data class ChartStyleConfig(
    val lines: List<ChartLineConfig>,
    val isScrollable: Boolean = false,
    val isClickable: Boolean = true,
    val xAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val yAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val tooltipBackgroundColor: Color = Color(0xFAFFFFFF),
    val tooltipBorderColor: Color = Color(0x15000000),
    val tooltipElevation: Dp = 6.dp,
    val legendPosition: LegendPosition = LegendPosition.TOP,
    val legendTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val chartPaddingTop: Dp = 20.dp,
    val chartPaddingBottom: Dp = 40.dp,
    val yAxisPaddingStart: Dp = 26.dp, 
    val axisLabelSpacing: Dp = 10.dp,
    val yAxisDividerColor: Color = Color(0xFFEEEEEE),
    val xAxisDividerColor: Color = Color(0xFFEEEEEE),
    val xAxisLabelRotation: Float = -45f,
    val legendItemSpacing: Dp = 24.dp,
    val legendShapeWidth: Dp = 40.dp,
    val legendShapeHeight: Dp = 12.dp,
    val tooltipDashColor: Color? = null,
    val tooltipDashIntervals: FloatArray = floatArrayOf(15f, 15f),
    val chartHeight: Dp = 180.dp,
    val bottomAxisHeight: Dp = 48.dp,
    val tooltipHorizontalPadding: Dp = 16.dp,
    val tooltipVerticalPadding: Dp = 10.dp,
    val tooltipInnerSpacing: Dp = 8.dp,
    val yAxisLabelPadding: Dp = 2.dp,
    val yAxisLabelColor: Color = Color.Gray,
    val xAxisLabelColor: Color = Color.Gray,
    val tooltipAutoDismissMs: Long? = null,
    val showTooltipCloseButton: Boolean = false
)
