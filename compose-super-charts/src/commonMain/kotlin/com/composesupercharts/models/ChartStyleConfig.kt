package com.composesupercharts.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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
    val alpha: Float
    val cap: StrokeCap
    val join: StrokeJoin
}

data class SolidLine(
    override val color: Color,
    override val width: Float = 4f,
    override val alpha: Float = 1f,
    override val cap: StrokeCap = StrokeCap.Butt,
    override val join: StrokeJoin = StrokeJoin.Miter
) : LineStyle {
    override val pathEffect: PathEffect? = null
}

data class DashedLine(
    override val color: Color,
    override val width: Float = 3f,
    val intervals: FloatArray = floatArrayOf(10f, 10f),
    override val alpha: Float = 1f,
    override val cap: StrokeCap = StrokeCap.Butt,
    override val join: StrokeJoin = StrokeJoin.Miter
) : LineStyle {
    override val pathEffect: PathEffect = PathEffect.dashPathEffect(intervals)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DashedLine) return false

        if (color != other.color) return false
        if (width != other.width) return false
        if (!intervals.contentEquals(other.intervals)) return false
        if (alpha != other.alpha) return false
        if (cap != other.cap) return false
        if (join != other.join) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + intervals.contentHashCode()
        result = 31 * result + alpha.hashCode()
        result = 31 * result + cap.hashCode()
        result = 31 * result + join.hashCode()
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
    val fillGradientColors: List<Color>? = null,
    val isVisible: Boolean = true
)

enum class NullPointBehavior {
    BreakSegment,
    SkipPoint,
    TreatAsZero
}

enum class AreaFillBehavior {
    CloseToBaselinePerSegment,
    ConnectAcrossSegments
}

enum class TooltipLayoutMode {
    Column,
    Row
}

data class LegendItemScope(
    val index: Int,
    val label: String,
    val lineConfig: ChartLineConfig,
    val isHidden: Boolean,
    val onToggle: (() -> Unit)?
)

data class ChartStyleConfig(
    val lines: List<ChartLineConfig>,
    val isScrollable: Boolean = false,
    val isClickable: Boolean = true,
    val xAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val yAxisLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipLabelTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val tooltipValueTextStyle: TextStyle = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    val tooltipBackgroundColor: Color = Color(0xFFFFFFFF),
    val tooltipBorderColor: Color = Color(0xFFE0E0E0),
    val tooltipElevation: Dp = 6.dp,
    val legendPosition: LegendPosition = LegendPosition.TOP,
    val legendTextStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    val chartPaddingTop: Dp = 20.dp,
    val chartPaddingBottom: Dp = 40.dp,
    val yAxisPaddingStart: Dp = 26.dp, 
    val axisLabelSpacing: Dp = 10.dp,
    val yAxisDividerColor: Color = Color.LightGray,
    val xAxisDividerColor: Color = Color.LightGray,
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
    val showTooltipCloseButton: Boolean = false,
    val animationDuration: Int = 500,
    val showCrosshair: Boolean = true,
    val allowLegendToggle: Boolean = false,
    val valueFormatter: ((Float) -> String)? = null,
    val yAxisTickFormatter: ((Float) -> String)? = null,
    val xAxisLabelFormatter: ((String, Int) -> String)? = null,
    val tooltipValueFormatter: ((Float) -> String)? = null,
    val accessibilityFormatter: ((Float) -> String)? = null,
    val nullPointBehavior: NullPointBehavior = NullPointBehavior.BreakSegment,
    val areaFillBehavior: AreaFillBehavior = AreaFillBehavior.CloseToBaselinePerSegment,
    val tooltipLayoutMode: TooltipLayoutMode = TooltipLayoutMode.Column,
    val showTooltipDivider: Boolean = false,
    val tooltipContent: (@Composable (selectedXIndex: Int, values: List<TooltipBubbleData>, onClose: () -> Unit) -> Unit)? = null,
    val legendItemRenderer: (@Composable (LegendItemScope) -> Unit)? = null,
    val legendMarkerRenderer: (@Composable (LegendItemScope) -> Unit)? = null,
    val legendModifier: Modifier = Modifier
)
