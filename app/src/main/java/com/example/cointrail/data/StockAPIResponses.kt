package com.example.cointrail.data

import kotlinx.serialization.Serializable

//Search results

@Serializable
data class Meta(
    val version: String = "",
    val status: Int = 0,
    val copywrite: String = "",
    val symbol: String = "",
    val processedTime: String = ""
)

@Serializable
data class StockSymbol(
    val shortname: String = "",
    val quoteType: String = "",
    val symbol: String = "",
    val index: String = "",
    val score: Int = 0,
    val typeDisp: String = "",
    val longname: String = "",
    val exchDisp: String = "",
    val sector: String = "",
    val sectorDisp: String = "",
    val industry: String = "",
    val industryDisp: String = "",
    val dispSecIndFlag: Boolean = false
)

@Serializable
data class StockSymbolSearchResponse(
    val meta: Meta = Meta(),
    val body: List<StockSymbol> = emptyList()
)

//Stock details

@Serializable
data class FiftyTwoWeekHighLow(
    val label: String,
    val value: String
)

@Serializable
data class DayRange(
    val label: String,
    val value: String
)

@Serializable
data class KeyStats(
    val fiftyTwoWeekHighLow: FiftyTwoWeekHighLow,
    val dayrange: DayRange
)

@Serializable
data class PrimaryData(
    val lastSalePrice: String,
    val netChange: String,
    val percentageChange: String,
    val deltaIndicator: String,
    val lastTradeTimestamp: String,
    val isRealTime: Boolean,
    val bidPrice: String,
    val askPrice: String,
    val bidSize: String,
    val askSize: String,
    val volume: String,
    val currency: String? = null
)

@Serializable
data class Body(
    val symbol: String,
    val companyName: String,
    val stockType: String,
    val exchange: String,
    val primaryData: PrimaryData,
    val secondaryData: String? = null,
    val marketStatus: String,
    val assetClass: String,
    val keyStats: KeyStats
)

@Serializable
data class StockResponse(
    val meta: Meta,
    val body: Body
)

//Stock history
@Serializable
data class MetaData(
    val version: String,
    val status: Int,
    val copywrite: String,
    val ticker: String,
    val interval: String,
    val dividend: String,
)
@Serializable
data class BodyItem(
    val timestamp: String,
    val timestamp_unix: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Int,
)
@Serializable
data class HistoryResponse(
    val meta: MetaData,
    val body: List<BodyItem>,
)

