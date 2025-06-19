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
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*
import kotlin.math.roundToInt

enum class SpendingHistogramGranularity(val label: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year")
}

sealed class GroupKey {
    data class Day(val date: LocalDate) : GroupKey()
    data class Week(val year: Int, val week: Int) : GroupKey()
    data class Month(val year: Int, val month: Int) : GroupKey()
    data class Year(val year: Int) : GroupKey()
}

@Composable
fun SpendingHistogramGraph(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Theme colors
    val borderColor = MaterialTheme.colorScheme.outline
    val backgroundColor = MaterialTheme.colorScheme.background
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant
    val barColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground
    val popupBackground = MaterialTheme.colorScheme.surface
    val popupText = MaterialTheme.colorScheme.onSurface

    // User-selected granularity
    var granularity by remember { mutableStateOf(SpendingHistogramGranularity.WEEK) }

    // Group transactions based on granularity, using GroupKey
    val (groupedData, sortedKeys, keyToLabel) = remember(transactions, granularity) {
        when (granularity) {
            SpendingHistogramGranularity.DAY -> {
                val data = transactions
                    .filter { it.date != null }
                    .groupBy { GroupKey.Day(it.date!!.toLocalDate()) }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedBy { it.date }
                val labelMap = keys.associateWith { key ->
                    key.date.format(DateTimeFormatter.ofPattern("MMM dd"))
                }
                Triple(data, keys, labelMap)
            }
            SpendingHistogramGranularity.WEEK -> {
                val weekFields = WeekFields.ISO
                val data = transactions
                    .filter { it.date != null }
                    .groupBy {
                        val localDate = it.date!!.toLocalDate()
                        val week = localDate.get(weekFields.weekOfWeekBasedYear())
                        GroupKey.Week(localDate.year, week)
                    }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedWith(compareBy({ it.year }, { it.week }))
                val labelMap = keys.associateWith { key ->
                    val date = LocalDate.now()
                        .withYear(key.year)
                        .with(weekFields.weekOfWeekBasedYear(), key.week.toLong())
                        .with(weekFields.dayOfWeek(), 1)
                    date.format(DateTimeFormatter.ofPattern("MMM dd"))
                }
                Triple(data, keys, labelMap)
            }
            SpendingHistogramGranularity.MONTH -> {
                val data = transactions
                    .filter { it.date != null }
                    .groupBy {
                        val localDate = it.date!!.toLocalDate()
                        GroupKey.Month(localDate.year, localDate.monthValue)
                    }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedWith(compareBy({ it.year }, { it.month }))
                val labelMap = keys.associateWith { key ->
                    val date = LocalDate.of(key.year, key.month, 1)
                    date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ${key.year}"
                }
                Triple(data, keys, labelMap)
            }
            SpendingHistogramGranularity.YEAR -> {
                val data = transactions
                    .filter { it.date != null }
                    .groupBy {
                        val localDate = it.date!!.toLocalDate()
                        GroupKey.Year(localDate.year)
                    }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }
                val keys = data.keys.sortedBy { it.year }
                val labelMap = keys.associateWith { key -> key.year.toString() }
                Triple(data, keys, labelMap)
            }
        }
    }

    val maxSpending = groupedData.values.maxOrNull() ?: 1.0
    var selectedBar by remember { mutableStateOf<Pair<Int, Offset>?>(null) }

    Column(modifier = modifier) {
        // Granularity selector
        GranularitySelector(
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
                // --- Horizontally scrollable chart ---
                val scrollState = rememberScrollState()
                val barMinWidth = 56.dp
                val chartWidth = (barMinWidth * sortedKeys.size).coerceAtLeast(300.dp)
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(scrollState)
                ) {
                    ChartCanvas(
                        sortedKeys = sortedKeys,
                        groupedData = groupedData,
                        keyToLabel = keyToLabel,
                        maxSpending = maxSpending,
                        axisColor = axisColor,
                        barColor = barColor,
                        textColor = textColor,
                        onBarTap = { index, tapOffset ->
                            selectedBar = index to tapOffset
                        },
                        modifier = Modifier
                            .width(chartWidth)
                            .fillMaxHeight()
                            .semantics { contentDescription = "Spending histogram" }
                    )
                    selectedBar?.let { (index, offset) ->
                        val key = sortedKeys.getOrNull(index)
                        if (key != null) {
                            val amount = groupedData[key] ?: 0.0
                            BarPopup(
                                label = keyToLabel[key] ?: key.toString(),
                                amount = amount,
                                popupBackground = popupBackground,
                                popupText = popupText,
                                offset = offset
                            ) {
                                selectedBar = null
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GranularitySelector(
    selected: SpendingHistogramGranularity,
    onSelect: (SpendingHistogramGranularity) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        SpendingHistogramGranularity.entries.forEach { granularity ->
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

@Composable
private fun ChartCanvas(
    sortedKeys: List<GroupKey>,
    groupedData: Map<out GroupKey, Double>,
    keyToLabel: Map<out GroupKey, String>,
    maxSpending: Double,
    axisColor: Color,
    barColor: Color,
    textColor: Color,
    onBarTap: (Int, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(sortedKeys, groupedData) {
            detectTapGestures { tapOffset ->
                if (sortedKeys.isEmpty()) return@detectTapGestures
                val barWidth = size.width / sortedKeys.size
                val index = (tapOffset.x / barWidth).toInt().coerceIn(0, sortedKeys.lastIndex)
                onBarTap(index, tapOffset)
            }
        }
    ) {
        if (sortedKeys.isEmpty()) return@Canvas

        val barWidth = size.width / sortedKeys.size
        val spacing = barWidth * 0.1f

        // Draw axes
        drawLine(axisColor, Offset(0f, size.height), Offset(size.width, size.height), 2f)
        drawLine(axisColor, Offset(0f, 0f), Offset(0f, size.height), 2f)

        // Draw X-axis labels
        sortedKeys.forEachIndexed { index, key ->
            val xPos = index * barWidth + barWidth / 2
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

        // Draw bars
        sortedKeys.forEachIndexed { index, key ->
            val amount = groupedData[key] ?: 0.0
            val barHeight = (amount / maxSpending * size.height * 0.8f).toFloat()
            drawRect(
                barColor,
                topLeft = Offset(index * barWidth + spacing / 2, size.height - barHeight),
                size = Size(barWidth - spacing, barHeight)
            )
        }
    }
}

@Composable
private fun BarPopup(
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

// --- Timestamp helpers ---

private fun Timestamp.toLocalDate(): LocalDate =
    this.toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()

// --- Preview ---

@Preview
@Composable
fun PreviewSpendingHistogramGraph() {
    CoinTrailTheme {
       // SpendingHistogramGraph(transactions = dummyTransactions)
    }
}
