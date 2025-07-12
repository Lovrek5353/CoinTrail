package com.example.cointrail.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Stock(
    @DocumentId var id: String? = null,
    val name: String="",
    val symbol: String="",
    val originalPrice: Double=0.0,
    val currentPrice: Double=0.0,
    val amount: Double=0.0,
    val purchaseDate: Timestamp? = null,
    val currency: String = "USD",
    val notes: String = "",
    val sector: String = "",
    val exchange: String = "",
    val isin: String = "",
    val dividendsReceived: Double = 0.0,
    val targetPrice: Double? = null
)