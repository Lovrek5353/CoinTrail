package com.example.cointrail.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable

data class Asset(
    @DocumentId var id: String? = null,
    val name: String="",
    val symbol: String="",
    val originalPrice: Double=0.0,
    val currentPrice: Double=0.0,
    val amount: Double=0.0,
    val type: String = "Stock",  // "Stock" or "ETF"
    val purchaseDate: Timestamp? = null,
    val currency: String = "USD",
    val notes: String = "",
    val sector: String = "",
    val exchange: String = "",
    val isin: String = "",
    val dividendsReceived: Double = 0.0,
    val targetPrice: Double? = null,
    val underlyingIndex: String = "",
    val expenseRatio: Double? = null,
    val issuer: String = ""
)

data class AssetSearch(
    val symbol: String,
    val name: String,
    val exchange: String,
    val type: String,
    val exchDisp: String,
)

fun StockSymbol.toAssetSearch()=AssetSearch(
    symbol=symbol,
    name=name,
    exchange=exch,
    type=type,
    exchDisp=exchDisp
)