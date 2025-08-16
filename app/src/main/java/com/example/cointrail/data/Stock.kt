package com.example.cointrail.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

data class Stock(
    @DocumentId var id: String? = null,
    val name: String="",
    val symbol: String="",
    val originalPrice: Double=0.0,
    var currentStockPrice: Double=0.0, //stock*amount
    val currentPrice: Double=0.0,
    val amount: Double=0.0,
    val purchaseDate: Timestamp? = null,
    val currency: String = "USD",
    val notes: String = "",
    val sector: String = "",
    val exchange: String = "",
    val dividendsReceived: Double = 0.0,
    val targetPrice: Double? = null,
    val deltaIndicator: String = "",
    val netChange: String = "",
    val userID: String=""
)

data class StockDetailsResponse(
    val lastSalePrice: String="",
    val netChange: String="",
    val percentageChange: String="",
    val deltaIndicator: String="",
    val currency: String="",
)

fun PrimaryData.toStockDetailsResponse()=StockDetailsResponse(
    lastSalePrice=lastSalePrice,
    netChange=netChange,
    percentageChange=percentageChange,
    deltaIndicator=deltaIndicator,
    currency=currency ?: ""
)

fun Body.toStock()=Stock(
    symbol = symbol,
    name = companyName,
    exchange = exchange,
    currentPrice = primaryData.lastSalePrice
        .replace("$", "")
        .replace(",", "")
        .trim()
        .toDoubleOrNull() ?: 0.0,
    currency = primaryData.currency ?: "",
    deltaIndicator = primaryData.deltaIndicator,
    netChange = primaryData.netChange
)
