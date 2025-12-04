package com.musicstreaming.data.api

import com.musicstreaming.data.model.SearchResult
import com.musicstreaming.data.model.Track
import com.musicstreaming.data.model.YouTubeVideo
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicApi {
    @GET("/api/tracks")
    suspend fun getTrendingTracks(): List<Track>

    @GET("/api/search")
    suspend fun searchTracks(@Query("q") query: String): SearchResult

    @GET("/api/track/{id}")
    suspend fun getTrackDetails(@Path("id") trackId: String): Track

    @GET("/api/recommendations")
    suspend fun getRecommendations(@Query("limit") limit: Int = 20): List<Track>
}

interface YouTubeApi {
    @GET("/youtube/search")
    suspend fun searchVideos(
        @Query("q") query: String,
        @Header("Authorization") token: String
    ): List<YouTubeVideo>

    @GET("/youtube/live")
    suspend fun getLiveStreams(
        @Header("Authorization") token: String
    ): List<YouTubeVideo>
}
