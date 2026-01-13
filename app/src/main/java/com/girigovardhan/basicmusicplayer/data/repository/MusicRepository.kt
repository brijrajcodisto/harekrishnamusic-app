package com.girigovardhan.basicmusicplayer.data.repository

import com.girigovardhan.basicmusicplayer.data.model.Song
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import com.girigovardhan.basicmusicplayer.data.local.SongDao
import kotlinx.coroutines.flow.Flow

class MusicRepository(
    private val songDao: SongDao,
    private val client: HttpClient
) {
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()

    suspend fun getSongs(): List<Song> {
        return try {
            return client.get("http://10.0.2.2:3001/tracks").body()
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Serialization Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun refreshSongs() {
        try {
            // 1. Fetch from Network
            val response = client.get("http://10.0.2.2:3001/tracks")
            val networkSongs: List<Song> = response.body()

            // 2. Save to Local Database (This automatically triggers the Flow above)
            songDao.insertSongs(networkSongs)
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Network failed: ${e.message}")
        }
    }
}