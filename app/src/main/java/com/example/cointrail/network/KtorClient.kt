package com.example.cointrail.network

import android.util.Log
import com.example.cointrail.network.HTTPRoutes.apiKey
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.* // Important: For content negotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.* // Important: For JSON serialization using kotlinx.serialization
import kotlinx.serialization.json.Json // Important: For configuring the JSON parser

object KtorClient {
    val httpClient: HttpClient = HttpClient(Android) {
        // Logging plugin (good for debugging network requests)
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP_Client", message) // Changed tag for clarity
                }
            }
            level = LogLevel.ALL // Log all request and response details
        }
        defaultRequest {
            header("x-rapidapi-key", apiKey)
            header("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com")
            // Add more headers if needed
        }

        install(ContentNegotiation) {
            // Configure JSON serialization using kotlinx.serialization
            json(Json {
                prettyPrint = true // Makes the JSON output in logs more readable
                isLenient = true // Allows for some leniency in JSON parsing (e.g., unquoted keys)
                ignoreUnknownKeys = true // Crucial: Ignores fields in JSON that are not present in your data class
            })
        }

        // Optional: Request timeout configuration
        // install(HttpTimeout) {
        //     requestTimeoutMillis = 15000 // 15 seconds
        //     connectTimeoutMillis = 15000
        //     socketTimeoutMillis = 15000
        // }

    }
}