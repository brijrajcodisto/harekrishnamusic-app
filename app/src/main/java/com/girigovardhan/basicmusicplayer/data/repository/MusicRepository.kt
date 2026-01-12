package com.girigovardhan.basicmusicplayer.data.repository

import com.girigovardhan.basicmusicplayer.data.model.Song
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.girigovardhan.basicmusicplayer.BuildConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.util.reflect.typeInfo

class MusicRepository {

    private val client = HttpClient(Android) {
        // This plugin handles converting JSON to your Song objects automatically
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Prevents crashing if API sends extra data
                prettyPrint = true
                isLenient = true
            })
        }

        defaultRequest {
            header("X-API-Key", BuildConfig.MUSIC_API_KEY) // BuildConfig.MUSIC_API_KEY
            header("X-API-Secret", BuildConfig.MUSIC_API_SECRET) // BuildConfig.MUSIC_API_SECRET
            // Optional: If your API also requires Content-Type
            header("Content-Type", "application/json")
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.d("KtorClient", message)
                }
            }
        }
    }

    suspend fun getSongs(): List<Song> {
        return try {
            return client.get("http://10.0.2.2:3001/tracks").body()
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Serialization Error: ${e.message}")
            emptyList()
        }
    }
}