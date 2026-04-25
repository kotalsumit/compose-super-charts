package com.example.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composesupercharts.components.organisms.CandlestickChart
import com.composesupercharts.models.CandleEntry
import com.composesupercharts.models.CandlestickChartData
import com.composesupercharts.models.CandlestickChartStyleConfig
import kotlin.random.Random

@Composable
fun CandlestickChartScreen(onBack: () -> Unit) {
    val random = remember { Random(123) }
    
    val entries = remember {
        var currentPrice = 100f
        List(25) { i ->
            val open = currentPrice
            val close = currentPrice + (random.nextFloat() - 0.5f) * 30f
            val high = maxOf(open, close) + random.nextFloat() * 15f
            val low = minOf(open, close) - random.nextFloat() * 15f
            currentPrice = close
            CandleEntry(open, high, low, close, if (i % 5 == 0) "Day $i" else "")
        }
    }

    var isScrollable by remember { mutableStateOf(false) }

    val styleConfig = CandlestickChartStyleConfig(
        padding = 40.dp,
        isScrollable = isScrollable,
        candleWidth = 24.dp,
        tooltipBackgroundColor = MaterialTheme.colorScheme.surface,
        tooltipLabelTextStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        tooltipValueTextStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        ChartScreenHeader(
            title = "Candlestick Demo",
            description = "OHLC price movement with bullish and bearish candles, scroll, and edge-aware tooltips.",
            onBack = onBack
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp).height(300.dp)) {
                CandlestickChart(
                    data = CandlestickChartData(entries),
                    config = styleConfig,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isScrollable, onCheckedChange = { isScrollable = it })
            Text("Scrollable", style = MaterialTheme.typography.bodyLarge)
        }

    }
}
