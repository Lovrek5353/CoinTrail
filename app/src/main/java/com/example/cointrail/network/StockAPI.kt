package com.example.cointrail.network

import android.util.Log
import com.example.cointrail.data.HistoryResponse
import com.example.cointrail.data.StockResponse
import com.example.cointrail.data.StockSymbolSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import com.example.cointrail.network.HTTPRoutes.searchAsset
import com.example.cointrail.network.HTTPRoutes.stockDetails
import com.example.cointrail.network.HTTPRoutes.stockHistory
import io.ktor.client.call.body

interface StockAPI {
    suspend fun searchAsset(searchString: String): StockSymbolSearchResponse
    suspend fun getAssetDetails(symbol: String, type: String): StockResponse
    suspend fun fetchAssetHistory(symbol: String): HistoryResponse
}

internal class StockAPIImpl(private val client: HttpClient) : StockAPI{

    override suspend fun searchAsset(searchString: String): StockSymbolSearchResponse {
        return try{
            val response: HttpResponse =
                client.get(searchAsset){
                    url{
                        parameters.append("search", searchString)
                    }
                }
            if(response.status.value==200){
                response.body<StockSymbolSearchResponse>()
            }else {
                val errorBody = response.bodyAsText()
                Log.e("YahooFinanceRepo", "API Error: ${response.status.value} - $errorBody")
                throw Exception("API Error: ${response.status.value} - $errorBody")
            }
        } catch (e: Exception){
            throw e
        }
    }
    override suspend fun getAssetDetails(symbol: String, type: String): StockResponse {
        return try {
            val response: HttpResponse =
                client.get(stockDetails) {
                    url {
                        parameters.append("ticker", symbol)
                        parameters.append("type", type)
                    }
                }
            if (response.status.value == 200) {
                response.body<StockResponse>()
            } else {
                val errorBody = response.bodyAsText()
                Log.e("YahooFinanceRepo", "API Error: ${response.status.value} - $errorBody")
                throw Exception("API Error: ${response.status.value} - $errorBody")
            }
        }catch (e: Exception){
            throw e
        }
    }
    override suspend fun fetchAssetHistory(symbol: String): HistoryResponse {
        return try {
            val response: HttpResponse =
                client.get(stockHistory){
                    url{
                        parameters.append("symbol", symbol)
                        parameters.append("interval","1h")
                        parameters.append("limit","750")
                        parameters.append("dividend","false")
                    }
                }
            if(response.status.value==200){
                response.body<HistoryResponse>()
            }else {
                val errorBody = response.bodyAsText()
                Log.e("YahooFinanceRepo", "API Error: ${response.status.value} - $errorBody")
                throw Exception("API Error: ${response.status.value} - $errorBody")
            }
        } catch (e: Exception){
            throw e
        }
    }
}

