package com.musicstreaming.data.api

import com.musicstreaming.data.model.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApi {
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): TrackResponse
}