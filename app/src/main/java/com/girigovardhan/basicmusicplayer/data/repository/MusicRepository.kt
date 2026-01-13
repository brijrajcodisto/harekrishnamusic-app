package com.girigovardhan.basicmusicplayer.data.repository

import androidx.paging.PagingSource
import com.girigovardhan.basicmusicplayer.data.local.MusicPrefs
import com.girigovardhan.basicmusicplayer.data.model.Song
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import com.girigovardhan.basicmusicplayer.data.local.SongDao
import com.girigovardhan.basicmusicplayer.data.model.SongDto
import com.girigovardhan.basicmusicplayer.data.model.VersionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicRepository(
    private val songDao: SongDao,
    private val client: HttpClient,
    private val musicPrefs: MusicPrefs
) {
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    suspend fun getSongs(): List<Song> {
        return try {
            return client.get("http://10.0.2.2:3001/tracks").body()
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Serialization Error: ${e.message}")
            emptyList()
        }
    }

    suspend fun refreshSongs() {
        _isSyncing.value = true
        try {

            val versionResponse: VersionResponse = client.get("http://10.0.2.2:3001/tracks/version").body()
            val remoteVersion = versionResponse.version.trim()

            if (remoteVersion == musicPrefs.getLocalVersion()) {
                // Log.d("MusicRepo", "Already up to date: $remoteVersion")
                return
            }

            val response: List<SongDto> = client.get("http://10.0.2.2:3001/tracks?limit=999999").body()

            // Map DTOs to Entities
            val entities = response.map { dto ->
                Song(
                    id = dto.id,
                    title = dto.title,
                    streamUrl = dto.streamUrl,
                    artistName = dto.artist?.name,
                    albumTitle = dto.album?.title,
                    coverUrl = dto.album?.coverUrl,
                    isLive = dto.isLive,
                    duration = dto.duration
                )
            }

            songDao.insertSongs(entities)
            musicPrefs.saveLocalVersion(remoteVersion)

        } catch (e: Exception) {
            _isSyncing.value = false
            e.printStackTrace()
        }
    }

    fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query)
    }

    fun getAllSongsPaged(): PagingSource<Int, Song> {
        return songDao.getAllSongsPaged()
    }

    fun searchSongsPaged(query: String): PagingSource<Int, Song> {
        return songDao.searchSongsPaged(query)
    }
}