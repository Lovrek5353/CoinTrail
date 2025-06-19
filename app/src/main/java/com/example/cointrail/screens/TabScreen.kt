package com.example.cointrail.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cointrail.R
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
        viewModel.fetchTabTransactions(tabID)
        viewModel.getTab(tabID)
    }
    val tab = viewModel.singleTab.collectAsState()
    val transactionList = viewModel.transactions.collectAsState()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = (tab.value?.name.toString()),
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
                    navController.navigate(Screen.TabEditorScreen.route)
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.Add))
            }
        }
    )
    {
        LazyColumn {
            item {
                Text(
                    text = tab.value?.name.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary)
        }
            item {
                Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding112)))
            }
            item{
                SpendingHistogramGraph(transactionList.value)
            }
            item{
                Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding50)))
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