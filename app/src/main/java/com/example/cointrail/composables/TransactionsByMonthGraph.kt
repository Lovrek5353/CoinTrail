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
import com.example.cointrail.ui.theme.CoinTrailTheme
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun TransactionsByMonthGraph(
    data: Map<String, Map<String, Double>>,
    modifier: Modifier = Modifier,
    incomeColor: Color = MaterialTheme.colorScheme.primary,
    expenseColor: Color = MaterialTheme.colorScheme.error
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val monthLabelFormatter = remember { DateTimeFormatter.ofPattern("MMM yyyy") }
    val sortedMonths = data.keys.sorted()
    val dataByMonth = sortedMonths.map { month -> month to (data[month] ?: emptyMap()) }

    val maxSum = (dataByMonth.flatMap { it.second.values }.maxOrNull() ?: 1.0)
        .takeIf { it > 0 } ?: 1.0

    // Store: index of bar group, "Income"/"Expenses", and tap position
    var selectedBar by remember { mutableStateOf<Triple<Int, String, Offset>?>(null) }

    Column(modifier = modifier) {
        Text(
            text = "Income vs Expenses by Month",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally)
        )
        Box(
            modifier = Modifier
                .padding(8.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .height(screenHeight / 2.5f)
                .padding(24.dp)
        ) {
            Row(Modifier.fillMaxSize()) {
                IncomeExpenseYAxisLabels(
                    maxValue = maxSum,
                    labelColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.width(48.dp).fillMaxHeight()
                )
                val scrollState = rememberScrollState()
                val monthCount = dataByMonth.size
                val barGroupMinWidth = 70.dp
                val chartWidth = (barGroupMinWidth * monthCount).coerceAtLeast(300.dp)
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(scrollState)
                ) {
                    TransactionsGroupedBarChart(
                        data = dataByMonth,
                        labelFormatter = { monthStr ->
                            // Expecting "yyyy-MM"
                            runCatching {
                                val parts = monthStr.split("-")
                                if (parts.size == 2) {
                                    val year = parts[0].toInt()
                                    val month = parts[1].toInt()
                                    java.time.LocalDate.of(year, month, 1).format(monthLabelFormatter)
                                } else monthStr
                            }.getOrDefault(monthStr)
                        },
                        maxSum = maxSum,
                        incomeColor = incomeColor,
                        expenseColor = expenseColor,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        onBarTap = { index, type, tapOffset ->
                            selectedBar = Triple(index, type, tapOffset)
                        },
                        modifier = Modifier
                            .width(chartWidth)
                            .fillMaxHeight()
                            .semantics { contentDescription = "Income Expense by Month Histogram" }
                    )
                    selectedBar?.let { (index, barType, tapOffset) ->
                        val (month, typesMap) = dataByMonth.getOrNull(index) ?: return@let
                        val amount = typesMap[barType] ?: 0.0
                        BarPopup(
                            label = "${barType}\n${runCatching {
                                val parts = month.split("-")
                                if (parts.size == 2) {
                                    val year = parts[0].toInt()
                                    val m = parts[1].toInt()
                                    java.time.LocalDate.of(year, m, 1).format(monthLabelFormatter)
                                } else month
                            }.getOrDefault(month)}",
                            amount = amount,
                            popupBackground = MaterialTheme.colorScheme.surface,
                            popupText = MaterialTheme.colorScheme.onSurface,
                            offset = tapOffset
                        ) {
                            selectedBar = null
                        }
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            LegendSwatch(label = "Income", color = incomeColor)
            Spacer(Modifier.width(16.dp))
            LegendSwatch(label = "Expenses", color = expenseColor)
        }
    }
}

@Composable
private fun TransactionsGroupedBarChart(
    data: List<Pair<String, Map<String, Double>>>,
    labelFormatter: (String) -> String,
    maxSum: Double,
    incomeColor: Color,
    expenseColor: Color,
    textColor: Color,
    onBarTap: (Int, String, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.pointerInput(data) {
            detectTapGestures { tapOffset ->
                if (data.isEmpty()) return@detectTapGestures
                val groupWidth = size.width / data.size
                val barSpacing = groupWidth * 0.2f
                val barWidth = (groupWidth - barSpacing) / 2
                val index = (tapOffset.x / groupWidth).toInt().coerceIn(0, data.lastIndex)
                val groupStart = index * groupWidth
                val barType = if (tapOffset.x - groupStart < barWidth) "Income" else "Expenses"
                onBarTap(index, barType, tapOffset)
            }
        }
    ) {
        if (data.isEmpty()) return@Canvas

        val groupWidth = size.width / data.size
        val barSpacing = groupWidth * 0.2f
        val barWidth = (groupWidth - barSpacing) / 2

        // Draw axes
        drawLine(textColor, Offset(0f, size.height), Offset(size.width, size.height), 2f)
        drawLine(textColor, Offset(0f, 0f), Offset(0f, size.height), 2f)

        // Draw X-axis labels & bars
        data.forEachIndexed { idx, (month, typesMap) ->
            val x0 = idx * groupWidth
            val income = typesMap["Income"] ?: 0.0
            val expenses = typesMap["Expenses"] ?: 0.0
            val maxH = maxOf(maxSum, 1.0)
            val incomeBarHeight = (income / maxH * size.height * 0.8f).toFloat()
            val expenseBarHeight = (expenses / maxH * size.height * 0.8f).toFloat()

            // Income bar (left)
            drawRect(
                incomeColor,
                topLeft = Offset(x0 + barSpacing / 2, size.height - incomeBarHeight),
                size = Size(barWidth, incomeBarHeight)
            )
            // Expense bar (right)
            drawRect(
                expenseColor,
                topLeft = Offset(x0 + barSpacing / 2 + barWidth, size.height - expenseBarHeight),
                size = Size(barWidth, expenseBarHeight)
            )

            // X label
            val middle = x0 + groupWidth / 2
            drawContext.canvas.nativeCanvas.drawText(
                labelFormatter(month),
                middle,
                size.height + 24f,
                android.graphics.Paint().apply {
                    color = textColor.toArgb()
                    textSize = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

@Composable
private fun IncomeExpenseYAxisLabels(
    maxValue: Double,
    labelColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        (4 downTo 0).forEach { i ->
            Text(
                text = "${(maxValue * i / 4).roundToInt()}",
                color = labelColor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun LegendSwatch(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(16.dp, 16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
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
            x = offset.x.toInt() - 60,
            y = offset.y.toInt() - 80
        ),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .background(popupBackground.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Preview(showBackground = true)
@Composable
fun PreviewTransactionsByMonthGraph() {
    val demoData = mapOf(
        "2025-05" to mapOf("Income" to 1200.0, "Expenses" to 950.0),
        "2025-06" to mapOf("Income" to 1700.0, "Expenses" to 1200.0),
        "2025-07" to mapOf("Income" to 2000.0, "Expenses" to 1750.0)
    )
    CoinTrailTheme {
        TransactionsByMonthGraph(data = demoData)
    }
}
