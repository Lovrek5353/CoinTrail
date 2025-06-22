package com.example.cointrail.screens

import ProgressIndicator
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.navigation.Screen
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.SavingPocketsViewModel
import com.example.cointrail.composables.SmallTransactionsTable
import com.example.cointrail.composables.SpendingHistogramGraph
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.cointrail.composables.SavingPocketSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingPocketScreen(
    savingPocketId: String,
    viewModel: SavingPocketsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(savingPocketId) {
        viewModel.fetchSavingPocket(savingPocketId)
        viewModel.observeTransactions(savingPocketId)
    }

    val savingPocket by viewModel.singleSavingPocket.collectAsState()
    val transactionList by viewModel.transactions.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = (savingPocket?.name.toString()),
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.SavingPocketTransactionEditor.createRoute(savingPocket?.id
                    ?: "Default ID"))
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
    )
    { innerPadding ->
        LazyColumn(
            modifier = modifier.padding(innerPadding)
        ) {
            item{
                savingPocket?.let {
                    Log.d("SavingPocketScreen", "Rendering ProgressIndicator with: ${it.name}") // Add this log
                    ProgressIndicator(it.balance, it.targetAmount)
                }
            }
            item {
                savingPocket?.let {
                    Log.d("SavingPocketScreen", "Rendering SavingPocketSummary with: ${it.name}") // Add this log
                    SavingPocketSummary(savingPocket=it)
                } ?: run {
                    Log.d("SavingPocketScreen", "SavingPocket is null, showing loading indicator.") // Add this log
                    // Show a loading indicator or placeholder
                    Text(
                        text = "Loading Saving Pocket Details...",
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding8))
                    )
                    // Optional: CircularProgressIndicator()
                }
            }
            item {
                SpendingHistogramGraph(transactionList)
            }
            item {
                SmallTransactionsTable(
                    transactionList,
                    onTransactionClick = { categoryId, transactionId ->
                        navController.navigate(
                            Screen.TransactionScreen.createRoute(
                                categoryId,
                                transactionId
                            )
                        )
                    }
                )
            }
            // add additional details if needed
        }
    }
}


@Preview
@Composable
fun SavingPocketScreenPreview() {
    CoinTrailTheme {
        //SavingPocketScreen()
    }
}