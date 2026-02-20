package com.composesupercharts.components.atoms

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun ChartLegendShape(
    modifier: Modifier,
    color: Color,
    lineStrokeWidth: Float = 3f,
    pointRadius: Float = 4f,
    isHollow: Boolean = false,
    pathEffect: androidx.compose.ui.graphics.PathEffect? = null
) {
    Canvas(modifier = modifier) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = lineStrokeWidth,
            pathEffect = pathEffect
        )

        if (isHollow) {
            drawCircle(
                color = color,
                radius = pointRadius,
                center = Offset(0f, size.height / 2),
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = Color.White,
                radius = pointRadius - 1f,
                center = Offset(0f, size.height / 2),
                style = Fill
            )
        } else {
            drawCircle(
                color = color,
                radius = pointRadius,
                center = Offset(0f, size.height / 2)
            )
        }

        if (isHollow) {
            drawCircle(
                color = color,
                radius = pointRadius,
                center = Offset(size.width, size.height / 2),
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = Color.White,
                radius = pointRadius - 1f,
                center = Offset(size.width, size.height / 2),
                style = Fill
            )
        } else {
            drawCircle(
                color = color,
                radius = pointRadius,
                center = Offset(size.width, size.height / 2)
            )
        }
    }
}
