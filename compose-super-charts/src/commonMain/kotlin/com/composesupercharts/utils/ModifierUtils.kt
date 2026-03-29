package com.composesupercharts.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * A modifier that rotates a component and calculates its new layout bounds.
 * Useful for rotated axis labels in charts.
 */
fun Modifier.rotatedLayout(degrees: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val radians = degrees * PI / 180.0
    val cos = abs(cos(radians))
    val sin = abs(sin(radians))
    
    val newWidth = (placeable.width * cos + placeable.height * sin).toInt()
    val newHeight = (placeable.width * sin + placeable.height * cos).toInt()

    layout(newWidth, newHeight) {
        placeable.placeRelativeWithLayer(
            x = (newWidth - placeable.width) / 2,
            y = (newHeight - placeable.height) / 2
        ) {
            rotationZ = degrees
        }
    }
}
