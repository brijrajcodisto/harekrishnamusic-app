package com.musicstreaming.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    @SerialName("image_url")
    val imageUrl: String?,
    @SerialName("preview_url")
    val previewUrl: String?,
    @SerialName("stream_url")
    val streamUrl: String?
)

@Serializable
data class SearchResult(
    val tracks: List<Track>,
    val artists: List<Artist>,
    val playlists: List<Playlist>
)

@Serializable
data class Artist(
    val id: String,
    val name: String,
    val image: String?,
    val genres: List<String>?
)

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val image: String?,
    val tracks: List<Track>?
)

@Serializable
data class YouTubeVideo(
    val id: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val isLiveContent: Boolean
)

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val profileImage: String?,
    val createdAt: Long
)
