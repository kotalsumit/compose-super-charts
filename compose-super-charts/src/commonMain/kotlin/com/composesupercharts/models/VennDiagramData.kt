package com.composesupercharts.models

import androidx.compose.ui.graphics.Color

data class VennSet(
    val id: String,
    val label: String,
    val value: Float,
    val color: Color
)

data class VennIntersection(
    val setIds: Set<String>,
    val label: String,
    val value: Float? = null,
    val tooltipData: List<TooltipBubbleData>? = null
)

data class VennDiagramData(
    val sets: List<VennSet>,
    val intersections: List<VennIntersection> = emptyList()
)
