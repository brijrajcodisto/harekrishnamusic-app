package com.girigovardhan.basicmusicplayer.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.girigovardhan.basicmusicplayer.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<Song>>

    @Upsert
    suspend fun insertSongs(songs: List<Song>)

    @Query("DELETE FROM songs")
    suspend fun clearAll()
}