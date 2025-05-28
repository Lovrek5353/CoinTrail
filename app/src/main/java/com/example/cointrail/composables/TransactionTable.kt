package com.example.cointrail.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.dummyTransactions

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsTable(transactions: List<Transaction>) {
    val configuration = LocalConfiguration.current
    val tableHeight = (configuration.screenHeightDp.dp) / 2

    // Helper to format date as "MMM dd"
    fun formatDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString) // expects "yyyy-MM-dd"
            date.format(DateTimeFormatter.ofPattern("MMM dd"))
        } catch (e: Exception) {
            dateString // fallback if parsing fails
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(tableHeight)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Date",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            "Name",
                            modifier = Modifier.weight(1.2f),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Category",
                            modifier = Modifier.weight(1.5f),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            "Amount",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                Divider()
            }

            items(transactions) { transaction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatDate(transaction.date),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        transaction.description,
                        modifier = Modifier.weight(1.2f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        transaction.categoryId.toString(),
                        modifier = Modifier.weight(1.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "%.2f".format(transaction.amount),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Divider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionsTablePreview() {
    TransactionsTable(
        dummyTransactions
    )
}
