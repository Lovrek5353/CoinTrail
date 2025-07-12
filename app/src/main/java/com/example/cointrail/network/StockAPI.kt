package com.example.cointrail.network

import android.util.Log
import com.example.cointrail.data.StockSymbolSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import com.example.cointrail.network.HTTPRoutes.searchAsset
import io.ktor.client.call.body

interface StockAPI {
    suspend fun searchAsset(searchString: String): StockSymbolSearchResponse
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
}

