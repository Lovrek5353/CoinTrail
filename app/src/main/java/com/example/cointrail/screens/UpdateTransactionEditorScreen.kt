package com.example.cointrail.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.composables.DatePickerModal
import com.example.cointrail.data.Transaction
import com.example.cointrail.viewModels.TransactionViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTransactionEditorScreen(
    modifier: Modifier = Modifier,
    transactionID: String,
    viewModel: TransactionViewModel,
    navController: NavController
) {
    Log.d("UpdateTransactionEditorScreen", "transactionID: $transactionID")

    var showDatePicker by remember { mutableStateOf(false) }

    // Collect the transaction from the ViewModel
    val loadedTransaction by viewModel.transaction.collectAsState()
    // Store the original transaction for old value comparison
    val originalTransaction = remember { mutableStateOf<Transaction?>(null) }

    // Load the transaction when the screen is opened
    LaunchedEffect(transactionID) {
        if (transactionID.isNotEmpty() && transactionID != "Default ID") {
            viewModel.loadSingleTransaction(transactionID)
            Log.d("UpdateTransactionEditorScreen", "Calling fetchTransaction for: $transactionID")
        } else {
            // Handle invalid ID as needed
        }
    }

    // Store the first loaded transaction as the original
    LaunchedEffect(loadedTransaction) {
        if (loadedTransaction != null && originalTransaction.value == null) {
            originalTransaction.value = loadedTransaction
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.updateTransaction),
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.savingPocketTransactionAmout),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                OutlinedTextField(
                    value = viewModel.transactionAmountString,
                    onValueChange = viewModel::onAmountStringUpdate,
                    label = { Text(text = stringResource(id = R.string.savingPocketTransactionAmout)) },
                    placeholder = { Text(text = stringResource(id = R.string.savingPocketTransactionAmout)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.transactionDate),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                val selectedDateMillis = viewModel.transactionDateMillis
                val formattedDate = selectedDateMillis?.let {
                    java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(it))
                } ?: stringResource(id = R.string.selectDatePrompt)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding16))
                        .clickable { showDatePicker = true }
                        .height(dimensionResource(id = R.dimen.padding48)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            item {
                if (showDatePicker) {
                    DatePickerModal(
                        onDateSelected = { millis ->
                            if (millis != null) {
                                viewModel.onDateSelected(millis)
                            }
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Text(
                    text = stringResource(id = R.string.transactionDescription),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                OutlinedTextField(
                    value = viewModel.transactionDescriptionString,
                    onValueChange = viewModel::onDescriptionStringUpdate,
                    label = { Text(text = stringResource(id = R.string.transactionDescription)) },
                )
            }
            item {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16)))
            }
            item {
                Button(onClick = {
                    val oldAmount = originalTransaction.value?.amount ?: 0.0
                    val newAmount = viewModel.transactionAmountString.toDoubleOrNull() ?: 0.0
                    val documentId=originalTransaction.value?.categoryId ?: ""

                    viewModel.updateTransaction()
                    viewModel.updateBalanceAfterUpdate(documentId, oldAmount, newAmount)
                    navController.popBackStack()
                    Log.d("TabTransactionEditor", "onSubmit clicked")
                }) {
                    Text(text = stringResource(id = R.string.add))
                }
            }
        }
    }
}
