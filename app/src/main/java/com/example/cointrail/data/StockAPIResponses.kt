package com.example.cointrail.data

import kotlinx.serialization.Serializable

@Serializable
data class StockSymbolSearchResponse(
    val meta: Meta,
    val body: List<StockSymbol>
)

@Serializable
data class Meta(
    val searchTerm: String,
    val processedTime: String
)

@Serializable
data class StockSymbol(
    val symbol: String,
    val name: String,
    val exch: String,
    val type: String,
    val exchDisp: String,
    val typeDisp: String
)
