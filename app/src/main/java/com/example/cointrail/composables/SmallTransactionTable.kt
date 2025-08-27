package com.example.cointrail.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.cointrail.R
import com.example.cointrail.data.Transaction
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallTransactionsTable(
    transactions: List<Transaction>,
    onTransactionClick: (categoryID: String, transactionID: String) -> Unit = { categoryID, transactionID -> }
) {
    val configuration = LocalConfiguration.current
    val tableHeight = (configuration.screenHeightDp.dp) / 2

    fun formatDate(timestamp: Timestamp?): String {
        return timestamp?.toLocalDate()?.format(DateTimeFormatter.ofPattern("MMM dd")) ?: "N/A"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(tableHeight)
            .border(
                width = dimensionResource(R.dimen.padding2),
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(dimensionResource(R.dimen.round12))
            )
            .padding(dimensionResource(R.dimen.padding8))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = dimensionResource(R.dimen.padding2)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(R.dimen.clip4)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.Date),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = stringResource(R.string.name),
                            modifier = Modifier.weight(1.5f),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding8)))
                        Text(
                            text = stringResource(R.string.amount),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                HorizontalDivider()
            }

            items(transactions) { transaction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding6))
                        .clickable { onTransactionClick(transaction.categoryId,
                            transaction.id.toString()
                        ) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatDate(transaction.date),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        transaction.description,
                        modifier = Modifier.weight(1.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding8)))
                    Text(
                        "%.2f".format(transaction.amount),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider()
            }
        }
    }
}

private fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}


