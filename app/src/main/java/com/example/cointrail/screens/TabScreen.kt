package com.example.cointrail.screens

import TabSummary
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cointrail.R
import com.example.cointrail.composables.SmallTransactionsTable
import com.example.cointrail.composables.SpendingHistogramGraph
import com.example.cointrail.navigation.Screen
import com.example.cointrail.repository.RepositoryImpl
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.TabsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabScreen(
    modifier: Modifier = Modifier,
    tabID: String,
    viewModel: TabsViewModel,
    navController: NavController
){
    LaunchedEffect(tabID){
        viewModel.observeTransactions(tabID)
        viewModel.fetchTab(tabID)
    }

    Log.d("TabScreen", "Open a screen with TabID: $tabID")
    val tab by viewModel.singleTab.collectAsState()
    val transactionList = viewModel.transactions.collectAsState()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = (tab?.name.toString()),
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
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.TabTransactionEditorScreen.createRoute(tabID))
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.Add))
            }
        }
    )
    {innerPadding ->
        LazyColumn(
            modifier=modifier.padding(innerPadding)
        ) {
            item {
                tab?.let {
                    Log.d("TabScreen", "Rendering TabSummary with: ${it.name}") // Add this log
                    TabSummary(tab = it)
                } ?: run {
                    Log.d("TabScreen", "Tab is null, showing loading indicator.") // Add this log
                    // Show a loading indicator or placeholder
                    Text(
                        text = "Loading Tab Details...",
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding8))
                    )
                    // Optional: CircularProgressIndicator()
                }
            }

            item{
                SpendingHistogramGraph(transactionList.value)
            }
//            item{
//                Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding50)))
//            }
            item {
                SmallTransactionsTable(
                    transactionList.value,
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
        }
    }

}

@Preview
@Composable
fun TabScreenPreview(){
    val viewModel = TabsViewModel(repository = RepositoryImpl())
    val navController = rememberNavController()
    CoinTrailTheme {
        TabScreen(
            tabID = "1",
            viewModel = viewModel,
            navController = navController
        )
    }
}