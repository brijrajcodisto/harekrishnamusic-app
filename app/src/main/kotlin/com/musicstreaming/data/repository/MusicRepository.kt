package com.musicstreaming.data.repository

import com.musicstreaming.data.api.MusicApi
import com.musicstreaming.data.model.Track

class MusicRepository(private val api: MusicApi) {
    suspend fun searchTracks(query: String): List<Track> {
        return api.searchTracks(query).data
    }
}