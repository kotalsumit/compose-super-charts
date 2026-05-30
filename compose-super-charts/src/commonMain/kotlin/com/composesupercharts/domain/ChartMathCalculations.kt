package com.composesupercharts.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import com.composesupercharts.models.ChartPointData
import com.composesupercharts.models.NullPointBehavior
import kotlin.math.ceil

object ChartMathCalculations {

    fun calculateYAxisMetrics(points: List<ChartPointData>, requestedMaxY: Int): Pair<Float, Float> {
        if (points.isEmpty()) return Pair(0f, 0f)
        
        val rawMaxY = points.maxOf { point ->
            point.yValues.filterNotNull().maxOrNull() ?: 0f
        }
        val safeStepCount = requestedMaxY.coerceAtLeast(1)
        val stepValue = ceil(rawMaxY / safeStepCount).coerceAtLeast(1f)
        val calculatedMaxY = safeStepCount * stepValue
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
        nullPointBehavior: NullPointBehavior = NullPointBehavior.BreakSegment,
        selector: (ChartPointData) -> Float?
    ): List<Offset?> {
        val spacing = if (points.size > 1) size.width / (points.size - 1) else size.width
        
        return points.mapIndexed { index, point ->
            val value = selector(point) ?: if (nullPointBehavior == NullPointBehavior.TreatAsZero) 0f else null
            if (value != null) {
                Offset(x = spacing * index, y = valueToYCoordinate(value, calculatedMaxY, size.height))
            } else null
        }
    }

    fun generateLinePath(
        offsets: List<Offset?>,
        nullPointBehavior: NullPointBehavior = NullPointBehavior.BreakSegment
    ): Path {
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
                } else if (nullPointBehavior == NullPointBehavior.BreakSegment) {
                    isFirstValid = true
                }
            }
        }
    }

    fun generateLinePaths(
        offsets: List<Offset?>,
        nullPointBehavior: NullPointBehavior = NullPointBehavior.BreakSegment
    ): List<Path> {
        val segments = mutableListOf<Path>()
        var currentPath: Path? = null
        var hasPointInCurrentPath = false

        offsets.forEach { offset ->
            if (offset != null) {
                val path = currentPath ?: Path().also { currentPath = it }
                if (!hasPointInCurrentPath) {
                    path.moveTo(offset.x, offset.y)
                    hasPointInCurrentPath = true
                } else {
                    path.lineTo(offset.x, offset.y)
                }
            } else if (nullPointBehavior == NullPointBehavior.BreakSegment) {
                currentPath?.let { segments.add(it) }
                currentPath = null
                hasPointInCurrentPath = false
            }
        }

        currentPath?.let { segments.add(it) }
        return segments
    }

    fun generateFilledPath(
        offsets: List<Offset?>, 
        height: Float,
        nullPointBehavior: NullPointBehavior = NullPointBehavior.BreakSegment
    ): Path {
        val validOffsets = offsets.filterNotNull()
        val path = generateLinePath(offsets, nullPointBehavior)
        
        if (validOffsets.isNotEmpty()) {
            val firstX = validOffsets.first().x
            val lastX = validOffsets.last().x
            
            path.lineTo(lastX, height)
            path.lineTo(firstX, height)
            path.close()
        }
        return path
    }

    fun generateFilledPathsPerSegment(
        offsets: List<Offset?>,
        height: Float,
        nullPointBehavior: NullPointBehavior = NullPointBehavior.BreakSegment
    ): List<Path> {
        val paths = mutableListOf<Path>()
        var segment = mutableListOf<Offset>()

        fun closeSegment() {
            if (segment.isNotEmpty()) {
                val path = Path().apply {
                    segment.forEachIndexed { index, offset ->
                        if (index == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                    }
                    lineTo(segment.last().x, height)
                    lineTo(segment.first().x, height)
                    close()
                }
                paths.add(path)
                segment = mutableListOf()
            }
        }

        offsets.forEach { offset ->
            if (offset != null) {
                segment.add(offset)
            } else if (nullPointBehavior == NullPointBehavior.BreakSegment) {
                closeSegment()
            }
        }

        closeSegment()
        return paths
    }
}
