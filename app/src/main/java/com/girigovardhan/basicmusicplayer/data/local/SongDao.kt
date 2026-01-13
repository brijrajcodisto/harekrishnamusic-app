package com.girigovardhan.basicmusicplayer.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.girigovardhan.basicmusicplayer.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<Song>>

    // Search query using SQL LIKE
    @Query("SELECT * FROM songs WHERE title LIKE '%' || :searchQuery || '%' OR artistName LIKE '%' || :searchQuery || '%'")
    fun searchSongs(searchQuery: String): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongsPaged(): PagingSource<Int, Song>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchSongsPaged(query: String): PagingSource<Int, Song>

    @Upsert
    suspend fun insertSongs(songs: List<Song>)

    @Query("DELETE FROM songs")
    suspend fun clearAll()
}