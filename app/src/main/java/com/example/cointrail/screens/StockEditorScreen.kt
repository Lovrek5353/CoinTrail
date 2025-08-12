package com.example.cointrail.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.viewModels.StocksViewModel
import androidx.compose.material3.OutlinedTextField
import com.example.cointrail.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockEditorScreen(
    stockSymbol: String,
    navController: NavController,
    viewModel: StocksViewModel,
    modifier: Modifier=Modifier
){
    LaunchedEffect(stockSymbol) {
        viewModel.fetchStockDetails(stockSymbol, type = "STOCKS")
        viewModel.fetchStockHistory(stockSymbol)
    }
    val stock by viewModel.stockState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.assets),
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
    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                Text(
                    text = "Stock amount"
                )
            }
            item{
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding16)))
            }
            item{
                OutlinedTextField(
                    value = viewModel.amountString,
                    onValueChange = viewModel::onAmountInput,
                    label = { Text(text = stringResource(id = R.string.stockAmount)) },
                )
            }
            item{
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding16)))
            }
            item {
                Button(
                    onClick = {
                        viewModel.onStockAdd()
                    },
                    modifier = modifier
                        .fillMaxWidth(2f / 3f)
                        .aspectRatio(5f)
                ) {
                    Text(text = stringResource(id = R.string.add))
                }

            }
        }
    }
}