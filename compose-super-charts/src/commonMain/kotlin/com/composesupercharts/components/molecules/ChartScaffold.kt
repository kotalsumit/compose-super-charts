package com.composesupercharts.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composesupercharts.components.atoms.ChartSurface
import com.composesupercharts.components.atoms.ChartText

@Composable
fun ChartScaffold(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalSpacing: Dp = 12.dp,
    headerContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null,
    topOverlayContent: (@Composable BoxScope.() -> Unit)? = null,
    chartContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        headerContent?.invoke()
        Box(modifier = Modifier.fillMaxWidth()) {
            chartContent()
            topOverlayContent?.invoke(this)
        }
        footerContent?.invoke()
    }
}

@Composable
fun AnalyticsChartCard(
    modifier: Modifier = Modifier,
    title: String,
    summaryValue: String? = null,
    changeText: String? = null,
    backgroundColor: Color = Color.White,
    borderColor: Color? = Color(0xFFE6E8EF),
    elevation: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    titleTextStyle: TextStyle = TextStyle(color = Color(0xFF1F2937), fontSize = 14.sp, fontWeight = FontWeight.Medium),
    summaryTextStyle: TextStyle = TextStyle(color = Color(0xFF111827), fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
    changeTextStyle: TextStyle = TextStyle(color = Color(0xFF6B7280), fontSize = 12.sp, fontWeight = FontWeight.Normal),
    headerActionContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null,
    chartContent: @Composable () -> Unit
) {
    ChartSurface(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        elevation = elevation,
        shape = RoundedCornerShape(12.dp)
    ) {
        ChartScaffold(
            contentPadding = contentPadding,
            headerContent = {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ChartText(text = title, style = titleTextStyle)
                        summaryValue?.let { ChartText(text = it, style = summaryTextStyle) }
                        changeText?.let { ChartText(text = it, style = changeTextStyle) }
                    }
                    headerActionContent?.invoke()
                }
            },
            footerContent = footerContent,
            chartContent = chartContent
        )
    }
}
