package com.example.cointrail.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cointrail.R
import com.example.cointrail.navigation.Screen
import com.example.cointrail.ui.theme.CoinTrailTheme
import com.example.cointrail.viewModels.StocksViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.assetSearch),
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
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate(Screen.AssetSearchScreen.route) },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "Asset search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { TODO() },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "Portfolio")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { TODO() },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "Watchlist")
            }
        }
    }
}

@Preview
@Composable
fun PortfolioScreenPreview() {
    CoinTrailTheme {
        // Note: Preview doesn't support NavController so here we show just buttons without nav actions
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {}) {
                Text(text = "Go to First Screen")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}) {
                Text(text = "Go to Second Screen")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}) {
                Text(text = "Go to Third Screen")
            }
        }
    }
}
