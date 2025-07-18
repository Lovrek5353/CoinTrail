package com.example.cointrail.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
fun CategoryPieChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.outline
    )
) {
    val configuration = LocalConfiguration.current
    val halfScreenHeight = (configuration.screenHeightDp.dp) / 2

    val total = data.values.sum().coerceAtLeast(0.01)
    val entries = data.entries.toList()

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(halfScreenHeight),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(data) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val height = size.height
                            val radius = minOf(width, height) / 2f
                            val center = Offset(width / 2f, height / 2f)
                            val touchVector = offset - center
                            val distanceFromCenter = touchVector.getDistance()

                            if (distanceFromCenter > radius) {
                                selectedIndex = null
                                return@detectTapGestures
                            }

                            val angle = ((atan2(touchVector.y, touchVector.x) * 180f / PI + 360f) % 360f).toFloat()
                            var startAngle = 0f

                            entries.forEachIndexed { index, entry ->
                                val sweepAngle = (entry.value / total * 360f).toFloat()
                                if (angle >= startAngle && angle < (startAngle + sweepAngle)) {
                                    selectedIndex = index
                                    tapOffset = offset
                                    return@detectTapGestures
                                }
                                startAngle += sweepAngle
                            }
                            selectedIndex = null
                        }
                    }
            ) {
                drawPieChart(entries, total, colors)
            }

            selectedIndex?.let { index ->
                val entry = entries[index]
                PieSlicePopup(
                    label = entry.key,
                    value = entry.value,
                    percent = entry.value / total,
                    offset = tapOffset,
                    onDismiss = { selectedIndex = null }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        // LazyRow legend below
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(entries.size) { index ->
                val entry = entries[index]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Box(
                        Modifier
                            .size(14.dp)
                            .background(
                                color = getPieSliceColor(index, entries.size, colors),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = entry.key,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun getPieSliceColor(index: Int, entryCount: Int, colors: List<Color>): Color {
    return if (index < colors.size) {
        colors[index]
    } else {
        // Strong, visually distinct color for additional categories
        val hue = (index * 360f / entryCount) % 360f
        Color.hsv(hue, 0.85f, 1.0f)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPieChart(
    entries: List<Map.Entry<String, Double>>,
    total: Double,
    colors: List<Color>
) {
    val radius = size.minDimension / 2f
    val topLeft = Offset((size.width - radius * 2f) / 2f, (size.height - radius * 2f) / 2f)
    var startAngle = 0f

    entries.forEachIndexed { index, entry ->
        val sweep = ((entry.value / total) * 360f).toFloat()
        val sliceColor = getPieSliceColor(index, entries.size, colors)
        drawArc(
            color = sliceColor,
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = true,
            topLeft = topLeft,
            size = Size(radius * 2, radius * 2)
        )
        startAngle += sweep
    }
}

@Composable
private fun PieSlicePopup(
    label: String,
    value: Double,
    percent: Double,
    offset: Offset,
    onDismiss: () -> Unit
) {
    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(offset.x.toInt(), offset.y.toInt() - 80),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "$${"%.2f".format(value)} (${(percent * 100).roundToInt()}%)",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    val data = mapOf(
        "Food" to 220.0,
        "Travel" to 135.0,
        "Utilities" to 80.0,
        "Entertainment" to 65.0,
        "Others" to 40.0,
        "Invest" to 20.0,
        "Books" to 50.0,
        "Gifts" to 15.0,
        "Electronics" to 30.0,
        "Sports" to 25.0,
        "Health" to 45.0,
        "Home" to 55.0,
        "Transport" to 60.0,
        "Clothes" to 90.0,
        "Personal" to 75.0,
        "Charity" to 15.0,
        "Tech" to 35.0,
        "Learning" to 22.0,
        "Office" to 19.0,
        "Misc" to 41.0
    )
    MaterialTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            CategoryPieChart(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
