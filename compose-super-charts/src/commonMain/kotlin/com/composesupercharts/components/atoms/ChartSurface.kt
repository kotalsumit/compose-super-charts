package com.composesupercharts.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChartSurface(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 0.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val baseModifier = modifier
        .shadow(elevation, shape)
        .background(backgroundColor, shape)
    
    val finalModifier = if (borderColor != null) {
        baseModifier.border(borderWidth, borderColor, shape)
    } else {
        baseModifier
    }

    Box(modifier = finalModifier, content = content)
}
