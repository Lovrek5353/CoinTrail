package com.example.cointrail.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.dummyTransactions
import com.example.cointrail.ui.theme.CoinTrailTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.roundToInt

// Renamed from SpendingHistogramGranularity
enum class SpendingLineGraphGranularity(val label: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year")
}

// Renamed from GroupKey
sealed class LineGraphGroupKey {
    data class Day(val date: String) : LineGraphGroupKey()
    data class Week(val year: Int, val week: Int) : LineGraphGroupKey()
    data class Month(val year: Int, val month: Int) : LineGraphGroupKey()
    data class Year(val year: Int) : LineGraphGroupKey()
}

// Renamed from SpendingHistogramGraph
@Composable
fun SpendingLineGraph(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val borderColor = MaterialTheme.colorScheme.outline
    val backgroundColor = MaterialTheme.colorScheme.background
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant
    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground
    val popupBackground = MaterialTheme.colorScheme.surface
    val popupText = MaterialTheme.colorScheme.onSurface

    var granularity by remember { mutableStateOf(SpendingLineGraphGranularity.WEEK) }

    // Group transactions based on granularity, using LineGraphGroupKey
    val (groupedData, sortedKeys, keyToLabel) = remember(transactions, granularity) {
        when (granularity) {
            SpendingLineGraphGranularity.DAY -> {
                val formatter = DateTimeFormatter.ISO_DATE
                val data = transactions.groupBy { LineGraphGroupKey.Day(it.date) }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedBy { it.date }
                val labelMap = keys.associateWith { key ->
                    val dateStr = key.date
                    LocalDate.parse(dateStr, formatter).format(DateTimeFormatter.ofPattern("MMM dd"))
                }
                Triple(data, keys, labelMap)
            }
            SpendingLineGraphGranularity.WEEK -> {
                val weekFields = WeekFields.ISO
                val data = transactions.groupBy {
                    val (y, w) = it.date.toYearWeek()
                    LineGraphGroupKey.Week(y, w)
                }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedWith(compareBy({ it.year }, { it.week }))
                val labelMap = keys.associateWith { key ->
                    val (year, week) = key
                    val date = LocalDate.now()
                        .withYear(year)
                        .with(weekFields.weekOfWeekBasedYear(), week.toLong())
                        .with(weekFields.dayOfWeek(), 1)
                    date.format(DateTimeFormatter.ofPattern("MMM dd"))
                }
                Triple(data, keys, labelMap)
            }
            SpendingLineGraphGranularity.MONTH -> {
                val data = transactions.groupBy {
                    val (y, m) = it.date.toYearMonth()
                    LineGraphGroupKey.Month(y, m)
                }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedWith(compareBy({ it.year }, { it.month }))
                val labelMap = keys.associateWith { key ->
                    val (year, month) = key
                    val date = LocalDate.of(year, month, 1)
                    date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " $year"
                }
                Triple(data, keys, labelMap)
            }
            SpendingLineGraphGranularity.YEAR -> {
                val data = transactions.groupBy { LineGraphGroupKey.Year(it.date.toYear()) }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedBy { it.year }
                val labelMap = keys.associateWith { key -> key.year.toString() }
                Triple(data, keys, labelMap)
            }
        }
    }

    val maxSpending = groupedData.values.maxOrNull() ?: 1.0
    var selectedPoint by remember { mutableStateOf<Pair<Int, Offset>?>(null) }

    Column(modifier = modifier) {
        // Renamed from GranularitySelector
        LineGraphGranularitySelector(
            selected = granularity,
            onSelect = { granularity = it }
        )
        Box(
            modifier = Modifier
                .padding(8.dp)
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .height(screenHeight / 3)
                .padding(24.dp)
        ) {
            Row(Modifier.fillMaxSize()) {
                YAxisLabels(
                    maxSpending = maxSpending,
                    labelColor = textColor,
                    modifier = Modifier
                        .width(48.dp)
                        .fillMaxHeight()
                )
                val scrollState = rememberScrollState()
                val minPointSpacing = 56.dp
                val chartWidth = (minPointSpacing * sortedKeys.size).coerceAtLeast(300.dp)
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(scrollState)
                ) {
                    // Renamed from ChartCanvas
                    LineGraphCanvas(
                        sortedKeys = sortedKeys,
                        groupedData = groupedData,
                        keyToLabel = keyToLabel,
                        maxSpending = maxSpending,
                        axisColor = axisColor,
                        lineColor = lineColor,
                        pointColor = pointColor,
                        textColor = textColor,
                        onPointTap = { index, tapOffset ->
                            selectedPoint = index to tapOffset
                        },
                        modifier = Modifier
                            .width(chartWidth)
                            .fillMaxHeight()
                            .semantics { contentDescription = "Spending line graph" }
                    )
                    selectedPoint?.let { (index, offset) ->
                        val key = sortedKeys.getOrNull(index)
                        if (key != null) {
                            val amount = groupedData[key] ?: 0.0
                            // Renamed from BarPopup
                            LineGraphPopup(
                                label = keyToLabel[key] ?: key.toString(),
                                amount = amount,
                                popupBackground = popupBackground,
                                popupText = popupText,
                                offset = offset
                            ) {
                                selectedPoint = null
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LineGraphGranularitySelector(
    selected: SpendingLineGraphGranularity,
    onSelect: (SpendingLineGraphGranularity) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        SpendingLineGraphGranularity.entries.forEach { granularity ->
            val isSelected = granularity == selected
            Button(
                onClick = { onSelect(granularity) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(granularity.label)
            }
        }
    }
}

@Composable
private fun YAxisLabels(
    maxSpending: Double,
    labelColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        (4 downTo 0).forEach { i ->
            Text(
                text = "${(maxSpending * i / 4).roundToInt()}",
                color = labelColor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// Renamed from ChartCanvas
@Composable
private fun LineGraphCanvas(
    sortedKeys: List<LineGraphGroupKey>,
    groupedData: Map<out LineGraphGroupKey, Double>,
    keyToLabel: Map<out LineGraphGroupKey, String>,
    maxSpending: Double,
    axisColor: Color,
    lineColor: Color,
    pointColor: Color,
    textColor: Color,
    onPointTap: (Int, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(sortedKeys, groupedData) {
            detectTapGestures { tapOffset ->
                if (sortedKeys.isEmpty()) return@detectTapGestures
                val pointSpacing = size.width / (sortedKeys.size - 1).coerceAtLeast(1)
                val index = (tapOffset.x / pointSpacing).toInt().coerceIn(0, sortedKeys.lastIndex)
                onPointTap(index, tapOffset)
            }
        }
    ) {
        if (sortedKeys.isEmpty()) return@Canvas

        val pointSpacing = size.width / (sortedKeys.size - 1).coerceAtLeast(1)
        val points = sortedKeys.mapIndexed { index, key ->
            val amount = groupedData[key] ?: 0.0
            val x = index * pointSpacing
            val y = size.height - (amount / maxSpending * size.height * 0.8f).toFloat()
            Offset(x, y)
        }

        drawLine(axisColor, Offset(0f, size.height), Offset(size.width, size.height), 2f)
        drawLine(axisColor, Offset(0f, 0f), Offset(0f, size.height), 2f)

        sortedKeys.forEachIndexed { index, key ->
            val xPos = index * pointSpacing
            drawContext.canvas.nativeCanvas.drawText(
                keyToLabel[key] ?: key.toString(),
                xPos,
                size.height + 24f,
                android.graphics.Paint().apply {
                    color = textColor.toArgb()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        points.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = lineColor,
                start = start,
                end = end,
                strokeWidth = 4f
            )
        }

        points.forEach { point ->
            drawCircle(
                color = pointColor,
                center = point,
                radius = 8f
            )
        }
    }
}

// Renamed from BarPopup
@Composable
private fun LineGraphPopup(
    label: String,
    amount: Double,
    popupBackground: Color,
    popupText: Color,
    offset: Offset,
    onDismiss: () -> Unit
) {
    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(
            x = offset.x.toInt() - 40,
            y = offset.y.toInt() - 60
        ),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .background(popupBackground.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = popupText
                )
                Text(
                    text = "$${"%.2f".format(amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = popupText
                )
            }
        }
    }
}

// --- Helper functions ---

private fun String.toYearWeek(): Pair<Int, Int> {
    return try {
        val date = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
        val week = date.get(WeekFields.ISO.weekOfWeekBasedYear())
        Pair(date.year, week)
    } catch (e: Exception) {
        0 to 0
    }
}

private fun String.toYearMonth(): Pair<Int, Int> {
    return try {
        val date = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
        Pair(date.year, date.monthValue)
    } catch (e: Exception) {
        0 to 0
    }
}

private fun String.toYear(): Int {
    return try {
        val date = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
        date.year
    } catch (e: Exception) {
        0
    }
}

// Renamed from PreviewSpendingHistogramGraph
@Preview
@Composable
fun PreviewSpendingLineGraph() {
    CoinTrailTheme {
        SpendingLineGraph(transactions = dummyTransactions)
    }
}
