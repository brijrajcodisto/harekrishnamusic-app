package com.musicstreaming.data.model

data class TrackResponse(
    val data: List<Track>,
    val total: Int
)

data class Track(
    val id: Long,
    val title: String,
    val artist: Artist,
    val album: Album,
    val preview: String, // URL to 30s preview
    val duration: Int
)

data class Artist(
    val id: Long,
    val name: String
)

data class Album(
    val id: Long,
    val title: String,
    val cover: String
)