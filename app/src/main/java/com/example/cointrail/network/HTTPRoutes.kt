package com.example.cointrail.network

object HTTPRoutes {
    const val apiKey="0434fa6fd0msh9cb9b402d730a1ap1be231jsn9275e2dce746"
    const val baseURL="https://yahoo-finance15.p.rapidapi.com/api/"
    const val searchAsset="${baseURL}v1/markets/search"
    const val stockDetails="${baseURL}v1/markets/quote"
    const val stockHistory="${baseURL}v2/markets/stock/history"
}