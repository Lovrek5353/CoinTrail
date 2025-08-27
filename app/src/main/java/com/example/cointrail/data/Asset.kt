package com.example.cointrail.data

import com.example.cointrail.database.StockEntity
import com.google.firebase.firestore.DocumentId
import com.google.firebase.Timestamp


//Used for ETF & Indexes
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
    val exchDisp: String,
    val typeDispl: String,
)

fun StockSymbol.toAssetSearch()=AssetSearch(
    symbol=symbol,
    name=shortname,
    exchDisp=exchDisp,
    typeDispl=typeDisp
)

data class AssetHistory(
    val date: Long,
    val price: Double
)

fun BodyItem.toAssetHistory()=AssetHistory(
    date =timestamp_unix,
    price =close
)

fun AssetSearch.toStockEntity()= StockEntity(
    symbol = symbol,
    name = name,
    exchDisp = exchDisp,
    typeDispl = typeDispl
)

fun StockEntity.toAssetSearch()= AssetSearch(
    symbol=symbol,
    name=name,
    exchDisp=exchDisp,
    typeDispl=typeDispl
)