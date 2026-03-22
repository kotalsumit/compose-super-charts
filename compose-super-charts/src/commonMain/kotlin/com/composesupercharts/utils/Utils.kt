package com.composesupercharts.utils

import com.composesupercharts.models.UnitType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout


fun Float.formatWithUnit(unitType: UnitType): String {
    return when(unitType) {
        UnitType.PERCENTAGE -> "${this.toInt()}%"
        UnitType.CURRENCY -> "$${this.toInt()}"
        else -> this.toString()
    }
}

fun Color.getLightToDarkGradientShades() = listOf(
    this.copy(alpha = 0f),
    this.copy(alpha = 0.03f),
    this.copy(alpha = 0.06f)
)

fun Color.getAlphaGradientShade(expectedAlpha: Float) = this.copy(alpha = expectedAlpha)

fun Color.getDarkToLightGradientShades() = listOf(
    this.copy(alpha = 0.1f),
    this.copy(alpha = 0.03f),
    this.copy(alpha = 0f)
)

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }

