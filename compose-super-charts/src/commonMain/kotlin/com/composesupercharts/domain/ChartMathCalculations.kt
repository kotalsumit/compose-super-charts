package com.composesupercharts.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import com.composesupercharts.models.ChartPointData
import kotlin.math.ceil

object ChartMathCalculations {

    fun calculateYAxisMetrics(points: List<ChartPointData>, requestedMaxY: Int): Pair<Float, Float> {
        if (points.isEmpty()) return Pair(0f, 0f)
        
        val rawMaxY = points.maxOf { point ->
            point.yValues.maxOfOrNull { it ?: 0f } ?: 0f
        }
        val stepValue = ceil(rawMaxY / requestedMaxY)
        val calculatedMaxY = requestedMaxY * stepValue
        return Pair(calculatedMaxY, stepValue)
    }

    fun valueToYCoordinate(value: Float, calculatedMaxY: Float, height: Float): Float {
        val safeValue = value.coerceAtLeast(0f)
        val yRange = calculatedMaxY.coerceAtLeast(1f)
        return height - (safeValue / yRange * height)
    }

    fun generatePointOffsets(
        points: List<ChartPointData>,
        size: Size,
        calculatedMaxY: Float,
        selector: (ChartPointData) -> Float?
    ): List<Offset?> {
        val spacing = if (points.size > 1) size.width / (points.size - 1) else size.width
        
        return points.mapIndexed { index, point ->
            val value = selector(point)
            if (value != null) {
                Offset(x = spacing * index, y = valueToYCoordinate(value, calculatedMaxY, size.height))
            } else null
        }
    }

    fun generateLinePath(offsets: List<Offset?>): Path {
        return Path().apply {
            var isFirstValid = true
            offsets.forEach { offset ->
                if (offset != null) {
                    if (isFirstValid) {
                        moveTo(offset.x, offset.y)
                        isFirstValid = false
                    } else {
                        lineTo(offset.x, offset.y)
                    }
                }
            }
        }
    }

    fun generateFilledPath(
        offsets: List<Offset?>, 
        height: Float
    ): Path {
        val validOffsets = offsets.filterNotNull()
        val path = generateLinePath(offsets)
        
        if (validOffsets.isNotEmpty()) {
            val firstX = validOffsets.first().x
            val lastX = validOffsets.last().x
            
            path.lineTo(lastX, height)
            path.lineTo(firstX, height)
            path.close()
        }
        return path
    }
}
