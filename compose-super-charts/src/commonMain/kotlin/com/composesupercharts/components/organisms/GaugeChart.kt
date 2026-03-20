package com.composesupercharts.components.organisms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.composesupercharts.models.GaugeChartData
import com.composesupercharts.models.GaugeChartStyleConfig
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun GaugeChart(
    modifier: Modifier = Modifier,
    data: GaugeChartData,
    config: GaugeChartStyleConfig = GaugeChartStyleConfig()
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(data.currentValue) {
        animationProgress.animateTo(
            targetValue = (data.currentValue - data.minValue) / (data.maxValue - data.minValue),
            animationSpec = tween(config.animationDuration)
        )
    }

    BoxWithConstraints(
        modifier = modifier.padding(config.padding),
        contentAlignment = Alignment.Center
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val size = width.coerceAtMost(height)
        val arcRadius = (size / 2) - with(density) { config.arcThickness.toPx() / 2 }
        val center = Offset(width / 2, height / 2)

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = config.backgroundArcColor,
                startAngle = config.startAngle,
                sweepAngle = config.sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = Size(arcRadius * 2, arcRadius * 2),
                style = Stroke(width = config.arcThickness.toPx(), cap = StrokeCap.Round)
            )

            data.ranges.forEach { range ->
                val rangeStartAngle = config.startAngle + (range.min - data.minValue) / (data.maxValue - data.minValue) * config.sweepAngle
                val rangeSweepAngle = (range.max - range.min) / (data.maxValue - data.minValue) * config.sweepAngle
                
                drawArc(
                    color = range.color,
                    startAngle = rangeStartAngle,
                    sweepAngle = rangeSweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = config.arcThickness.toPx(), cap = StrokeCap.Butt)
                )
            }

            val currentAngle = config.startAngle + animationProgress.value * config.sweepAngle
            val angleRad = (currentAngle * PI / 180f).toDouble()
            
            val needleLength = arcRadius * 0.9f
            val needleEnd = Offset(
                center.x + (cos(angleRad) * needleLength).toFloat(),
                center.y + (sin(angleRad) * needleLength).toFloat()
            )

            val needlePath = Path().apply {
                val backAngle1 = ((currentAngle + 90) * PI / 180f).toDouble()
                val backAngle2 = ((currentAngle - 90) * PI / 180f).toDouble()
                val baseWidth = config.needleWidth.toPx()
                
                moveTo(needleEnd.x, needleEnd.y)
                lineTo(
                    center.x + (cos(backAngle1) * baseWidth).toFloat(),
                    center.y + (sin(backAngle1) * baseWidth).toFloat()
                )
                lineTo(
                    center.x + (cos(backAngle2) * baseWidth).toFloat(),
                    center.y + (sin(backAngle2) * baseWidth).toFloat()
                )
                close()
            }

            drawPath(
                path = needlePath,
                color = config.needleColor
            )

            drawCircle(
                color = config.needleColor,
                radius = config.needleBaseRadius.toPx(),
                center = center
            )

            val valueText = data.currentValue.toInt().toString()
            val unitText = data.unit ?: ""
            
            val valueLayout = textMeasurer.measure(valueText, config.valueTextStyle)
            val unitLayout = textMeasurer.measure(unitText, config.unitTextStyle)
            
            drawText(
                valueLayout,
                topLeft = Offset(
                    center.x - valueLayout.size.width / 2,
                    center.y + config.needleBaseRadius.toPx() + 8.dp.toPx()
                )
            )
            
            drawText(
                unitLayout,
                topLeft = Offset(
                    center.x - unitLayout.size.width / 2,
                    center.y + config.needleBaseRadius.toPx() + 8.dp.toPx() + valueLayout.size.height
                )
            )
        }
    }
}
