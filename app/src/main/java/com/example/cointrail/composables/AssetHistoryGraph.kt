package com.example.cointrail.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cointrail.data.AssetHistory
import com.example.cointrail.ui.theme.CoinTrailTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HistoryLineGraph(
    history: List<AssetHistory>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Theme colors
    val borderColor = MaterialTheme.colorScheme.outline
    val backgroundColor = MaterialTheme.colorScheme.background
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant
    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground

    // Layout
    val padding = 24.dp
    val yAxisLabelWidth = 48.dp
    val chartHeight = screenHeight / 3
    val minSegmentWidth = 56.dp
    val chartWidth = (minSegmentWidth * (history.size - 1)).coerceAtLeast(300.dp)

    // Data calculations
    val minPrice = history.minOfOrNull { it.price } ?: 0.0
    val maxPrice = history.maxOfOrNull { it.price } ?: 1.0
    val priceRange = (maxPrice - minPrice).takeIf { it != 0.0 } ?: 1.0

    Box(
        modifier = modifier
            .padding(8.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(chartHeight)
            .padding(padding)
    ) {
        Row(Modifier.fillMaxSize()) {
            // Y-axis labels
            YAxisLabels(
                minPrice = minPrice,
                maxPrice = maxPrice,
                labelColor = textColor,
                modifier = Modifier
                    .width(yAxisLabelWidth)
                    .fillMaxHeight()
            )
            // Scrollable chart area, initially scrolled to the end
            val scrollState = rememberScrollState()
            LaunchedEffect(history.size) {
                // Scroll to the end (right side) after composition
                scrollState.scrollTo(scrollState.maxValue)
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(chartWidth)
                        .fillMaxHeight()
                ) {
                    val graphWidth = size.width
                    val graphHeight = size.height
                    val xStep = graphWidth / (history.size - 1).coerceAtLeast(1)
                    val yStep = graphHeight / priceRange

                    // Draw axes
                    drawLine(
                        color = axisColor,
                        start = Offset(0f, graphHeight),
                        end = Offset(graphWidth, graphHeight),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = axisColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, graphHeight),
                        strokeWidth = 2f
                    )

                    // Draw X-axis labels
                    history.forEachIndexed { idx, item ->
                        val x = idx * xStep
                        val label = item.date.toHumanDate()
                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            x,
                            graphHeight + 28f,
                            android.graphics.Paint().apply {
                                color = textColor.toArgb()
                                textSize = 28f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }

                    // Draw line and points
                    for (i in 0 until history.size - 1) {
                        val x1 = i * xStep
                        val y1 = graphHeight - ((history[i].price - minPrice) * yStep).toFloat()
                        val x2 = (i + 1) * xStep
                        val y2 = graphHeight - ((history[i + 1].price - minPrice) * yStep).toFloat()
                        drawLine(
                            color = lineColor,
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 4f
                        )
                        drawCircle(
                            color = pointColor,
                            radius = 6f,
                            center = Offset(x1, y1)
                        )
                    }
                    // Draw last point
                    if (history.isNotEmpty()) {
                        val lastX = (history.size - 1) * xStep
                        val lastY = graphHeight - ((history.last().price - minPrice) * yStep).toFloat()
                        drawCircle(
                            color = pointColor,
                            radius = 6f,
                            center = Offset(lastX, lastY)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YAxisLabels(
    minPrice: Double,
    maxPrice: Double,
    labelColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val steps = 4
    val priceRange = maxPrice - minPrice
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        (steps downTo 0).forEach { i ->
            val value = minPrice + (priceRange * i / steps)
            Text(
                text = value.roundToInt().toString(),
                color = labelColor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

fun Long.toHumanDate(): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(Date(this * 1000))
}

@Preview(showBackground = true)
@Composable
fun HistoryLineGraphPreview() {
    CoinTrailTheme {
        val sampleData = listOf(
            AssetHistory(1689379200, 100.0),
            AssetHistory(1689465600, 101.5),
            AssetHistory(1689552000, 102.0),
            AssetHistory(1689638400, 99.5),
            AssetHistory(1689724800, 105.0)
        )
        HistoryLineGraph(history = sampleData)
    }
}
