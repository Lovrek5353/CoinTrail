package com.example.cointrail.screens

import AnalyticsViewModel
import CompareTwoMonthsTransactionsChart
import CompareTwoMonthsTransactionsChartPreview
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.composables.CategoryPieChart
import com.example.cointrail.composables.SpendingLineGraph
import com.example.cointrail.composables.TransactionsByMonthGraph
import com.example.cointrail.ui.theme.CoinTrailTheme



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    navController: NavController,
){
    val transactions by viewModel.transactions.collectAsState()
    val sumByType by viewModel.sumByType.collectAsState()
    val sumByCategory by viewModel.sumByCategory.collectAsState()
    val monthlyIncomeExpense by viewModel.monthlyIncomeExpense.collectAsState()
    val currentMonthTransactions by viewModel.currentMonthTransactionsWithName.collectAsState()
    val previousMonthTransactions by viewModel.previousMonthTransactionsWithName.collectAsState()

    Log.d("AnalyticsScreen",currentMonthTransactions.toString())
    Log.d("AnalyticsScreen",previousMonthTransactions.toString())
    if (transactions == null ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.Analytics),
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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = stringResource(R.string.transactionPerType),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            item {
                CategoryPieChart(sumByType)
            }
            item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16))) }
            item {
                Text(
                    text = stringResource(R.string.spendinghabits),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            item{
                CategoryPieChart(sumByCategory)
            }
            item { Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding16))) }
            item {
                Text(
                    text = stringResource(R.string.spendingHabitsMonth),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            item{
                TransactionsByMonthGraph(monthlyIncomeExpense)
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CompareTwoMonthsTransactionsChart(
                        currentMonthTransactions = currentMonthTransactions,
                        previousMonthTransactions = previousMonthTransactions,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }


}

//@Preview
//@Composable
//fun AnalyticsScreenPreview(){
//    CoinTrailTheme {
//        AnalyticsScreen()
//    }
//}
