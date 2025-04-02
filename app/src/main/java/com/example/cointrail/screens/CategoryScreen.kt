package com.example.cointrail.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.cointrail.ui.theme.CoinTrailTheme

@Composable
fun CategoryScreen(){
    TODO("Screen displaying category information, " +
            "transactions, statistics, button to add new transaction" +
    "It needs to be modular one file for all categories"
    )
}

@Preview
@Composable
fun CategoryScreenPreview() {
    CoinTrailTheme {
        CategoryScreen()
    }
}