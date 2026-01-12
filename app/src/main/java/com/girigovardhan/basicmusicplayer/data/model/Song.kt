package com.girigovardhan.basicmusicplayer.data.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Song(
    val id: String,
    val title: String,
    val artistId: String? = null,    // Null in JSON
    val albumId: String? = null,
    val duration: Int? = 0,
    val coverUrl: String? = null,   // Null in JSON

    // We map streamUrl from JSON to streamUrl in Kotlin
    val streamUrl: String,

    val lyrics: String? = null,
    val isLive: Boolean = false,
    val artist: String? = null,     // Null in JSON

    // Nested object: If you don't need it, ignoreUnknownKeys will skip it,
    // but including it as nullable makes the parser more stable.
    val album: Album? = null
)

@Serializable
data class Album(
    val id: String,
    val title: String,
    val artistId: String? = null,
    val coverUrl: String? = null
)