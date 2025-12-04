package com.musicstreaming.data.repository

import com.musicstreaming.data.api.MusicApi
import com.musicstreaming.data.model.SearchResult
import com.musicstreaming.data.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MusicRepository(private val musicApi: MusicApi) {

    fun getTrendingTracks(): Flow<Result<List<Track>>> = flow {
        try {
            val tracks = musicApi.getTrendingTracks()
            emit(Result.success(tracks))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun searchTracks(query: String): Flow<Result<SearchResult>> = flow {
        try {
            val result = musicApi.searchTracks(query)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getTrackDetails(trackId: String): Flow<Result<Track>> = flow {
        try {
            val track = musicApi.getTrackDetails(trackId)
            emit(Result.success(track))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getRecommendations(): Flow<Result<List<Track>>> = flow {
        try {
            val tracks = musicApi.getRecommendations()
            emit(Result.success(tracks))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
