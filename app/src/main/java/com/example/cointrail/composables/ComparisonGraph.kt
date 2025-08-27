package com.example.cointrail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.enums.TransactionType
import kotlin.math.max
import kotlin.math.roundToInt

private fun aggregateByCategory(transactions: List<Transaction>): Map<String, Double> =
    transactions.groupBy { it.categoryId }
        .mapValues { it.value.sumOf { tx -> tx.amount } }

@Composable
fun CompareTwoMonthsTransactionsChart(
    currentMonthTransactions: List<Transaction>,
    previousMonthTransactions: List<Transaction>,
    modifier: Modifier = Modifier,
    categoryIdToName: (String) -> String = { it },
) {
    val config = LocalConfiguration.current
    val containerHeight: Dp = config.screenHeightDp.dp * 0.4f
    val screenWidthDp = config.screenWidthDp.dp

    val chartWidthFraction = 0.54f
    val density = LocalDensity.current
    val barHeight = 26.dp
    val barMinWidth = 5.dp
    val labelMinInsideWidth = 38.dp

    val barAreaPx = with(density) { (screenWidthDp * chartWidthFraction).toPx() }
    val halfBarAreaPx = barAreaPx / 2

    val prevSums = remember(previousMonthTransactions) { aggregateByCategory(previousMonthTransactions) }
    val currSums = remember(currentMonthTransactions) { aggregateByCategory(currentMonthTransactions) }

    val allCategories = (prevSums.keys + currSums.keys).toSortedSet()
    val chartData = allCategories.map { catId ->
        Triple(
            categoryIdToName(catId),
            currSums[catId] ?: 0.0,
            prevSums[catId] ?: 0.0
        )
    }.sortedByDescending { it.second + it.third }

    val barColors = listOf(
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.primaryContainer
    )
    val barTextColors = listOf(
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.onPrimaryContainer
    )
    val labelColor = MaterialTheme.colorScheme.onSurface

    val maxValue = max(1.0, chartData.maxOfOrNull { max(it.second, it.third) } ?: 0.0)

    val totalCurr = chartData.sumOf { it.second }
    val totalPrev = chartData.sumOf { it.third }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Monthly Transactions Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    color = labelColor,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                // --- Centered Legend ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(width = 14.dp, height = 10.dp)
                                .background(barColors[0], MaterialTheme.shapes.small)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Current", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.width(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(width = 14.dp, height = 10.dp)
                                .background(barColors[1], MaterialTheme.shapes.small)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Previous", style = MaterialTheme.typography.bodySmall)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    items(chartData) { (label, currAmount, prevAmount) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(barHeight)
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category label
                            Text(
                                text = label,
                                modifier = Modifier.widthIn(min = 56.dp, max = 82.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // --- CURRENT MONTH BAR (LEFT, right-aligned in cell) ---
                                    Box(
                                        modifier = Modifier.weight(0.5f),
                                        contentAlignment = Alignment.CenterEnd,
                                    ) {
                                        val currPx = max(
                                            (halfBarAreaPx * (currAmount / maxValue)).toFloat(),
                                            with(density) { barMinWidth.toPx() }
                                        )
                                        val currDp = with(density) { currPx.toDp() }
                                        // Always visible value: outside if bar is too small
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.End,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            if (currDp <= labelMinInsideWidth) {
                                                Text(
                                                    text = currAmount.roundToInt().toString(),
                                                    color = labelColor,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(end = 6.dp)
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .height(barHeight)
                                                    .width(currDp)
                                                    .background(barColors[0], MaterialTheme.shapes.small),
                                                contentAlignment = Alignment.CenterEnd
                                            ) {
                                                if (currDp > labelMinInsideWidth) {
                                                    Text(
                                                        text = currAmount.roundToInt().toString(),
                                                        color = barTextColors[0],
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(end = 4.dp),
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    // --- CENTER LINE ---
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .fillMaxHeight()
                                            .background(MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                                    )
                                    // --- PREVIOUS MONTH BAR (RIGHT, left-aligned in cell) ---
                                    Box(
                                        modifier = Modifier.weight(0.5f),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        val prevPx = max(
                                            (halfBarAreaPx * (prevAmount / maxValue)).toFloat(),
                                            with(density) { barMinWidth.toPx() }
                                        )
                                        val prevDp = with(density) { prevPx.toDp() }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .height(barHeight)
                                                    .width(prevDp)
                                                    .background(barColors[1], MaterialTheme.shapes.small),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                if (prevDp > labelMinInsideWidth) {
                                                    Text(
                                                        text = prevAmount.roundToInt().toString(),
                                                        color = barTextColors[1],
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(start = 4.dp),
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                            if (prevDp <= labelMinInsideWidth) {
                                                Text(
                                                    text = prevAmount.roundToInt().toString(),
                                                    color = labelColor,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(start = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(9.dp))

                // --- TOTALS ROW ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelLarge,
                        color = labelColor,
                        modifier = Modifier.weight(0.18f),
                        textAlign = TextAlign.Start,
                    )

                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.extraLarge,
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = totalCurr.roundToInt().toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Start,
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.04f))
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.extraLarge,
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            text = totalPrev.roundToInt().toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompareTwoMonthsTransactionsChartPreview() {
    val prevTestTxs = listOf(
        Transaction("t1", 120.0, "food", null, "", TransactionType.WITHDRAWAL, "user1"),
        Transaction("t2", 80.0, "travel", null, "", TransactionType.WITHDRAWAL, "user1"),
        Transaction("t3", 45.0, "shopping", null, "", TransactionType.WITHDRAWAL, "user1"),
        Transaction("t4", 60.0, "bills", null, "", TransactionType.WITHDRAWAL, "user1"),
    )
    val currTestTxs = listOf(
        Transaction("t5", 200.0, "food", null, "", TransactionType.WITHDRAWAL, "user1"),
        Transaction("t6", 140.0, "travel", null, "", TransactionType.WITHDRAWAL, "user1"),
        Transaction("t7", 65.0, "bills", null, "", TransactionType.WITHDRAWAL, "user1"),
        Transaction("t8", 145.0, "health", null, "", TransactionType.WITHDRAWAL, "user1"),
    )
    val categoryNames = mapOf(
        "food" to "Food",
        "travel" to "Travel",
        "bills" to "Bills",
        "shopping" to "Shopping",
        "health" to "Health",
    )
    CompareTwoMonthsTransactionsChart(
        currentMonthTransactions = currTestTxs,
        previousMonthTransactions = prevTestTxs,
        categoryIdToName = { categoryNames[it] ?: it }
    )
}
