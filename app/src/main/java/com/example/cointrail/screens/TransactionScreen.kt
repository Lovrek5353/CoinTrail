package com.example.cointrail.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cointrail.R
import com.example.cointrail.data.Transaction
import com.example.cointrail.data.enums.TransactionType
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.TransactionViewModel
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactionID: String,
    categoryID: String,
    navController: NavController,
    onDelete: () -> Unit = {},
    onUpdate: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel
) {

    val transaction by viewModel.transaction.collectAsState()

    LaunchedEffect(categoryID, transactionID) {
        Log.d("TransactionScreen", "LaunchedEffect triggered for categoryID: $categoryID, transactionID: $transactionID")
        viewModel.loadTransaction(categoryID, transactionID)

        Log.d("TransactionScreen", "Transaction: $transaction") // This log will run on every recomposition
    }

    Log.d("TransactionScreen", "Transaction: $transaction")


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.transaction),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.backIcon),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.padding24)),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = RoundedCornerShape(dimensionResource(R.dimen.round20)),
                elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.padding8)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding24)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding16))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = transaction?.description?: "No Description",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(dimensionResource(R.dimen.size36))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_transaction),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(
                            onClick = onUpdate,
                            modifier = Modifier.size(dimensionResource(R.dimen.size36))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit_transaction),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    TransactionDetailRow(
                        label = "Amount",
                        value = transaction?.amount?.let { "€%.2f".format(it) } ?: "No amount",  //add way to implement currency symbol and tracking in the entire app
                        color = if ((transaction?.type
                                ?: TransactionType.DEPOSIT) == TransactionType.DEPOSIT
                        ) Color(0xFF388E3C) else Color(0xFFD32F2F)
                    )
                    TransactionDetailRow("Category", transaction?.categoryId ?: "No category")
                    TransactionDetailRow("Type", transaction?.type?.name ?: "No type")
                    TransactionDetailRow("Date", formatDate(transaction?.date))
                    TransactionDetailRow("User ID", transaction?.userID ?: "No userID")  //Replace with user name or email - specifically for shared transactions
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

private fun formatDate(timestamp: Timestamp?): String {
    return timestamp?.toDate()
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ?: "N/A"
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    val navController = rememberNavController()
    CoinTrailTheme {
//        TransactionScreen(
//            transaction = Transaction(
//                id = "TX123456",
//                amount = 42.50,
//                categoryId = "Groceries",
//                date = Timestamp.now(),
//                description = "Weekly supermarket shopping",
//                type = TransactionType.WITHDRAWAL,
//                userID = "user_001"
//            ),
//            navController = navController,
//            onDelete = { /* Handle delete */ },
//            onUpdate = { /* Handle edit */ }
//        )
    }
}
