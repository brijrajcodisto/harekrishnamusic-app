package com.girigovardhan.basicmusicplayer.data.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// --- DTO: Used ONLY for Ktor/JSON ---
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SongDto(
    val id: String,
    val title: String,
    val streamUrl: String,
    val artist: ArtistDto? = null,
    val album: AlbumDto? = null,
    val isLive: Boolean = false,
    val duration: Int? = 0
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ArtistDto(val name: String? = null)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AlbumDto(val title: String? = null, val coverUrl: String? = null)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class VersionResponse(
    val version: String
)

// --- Entity: Used ONLY for Room Database ---
// REMOVE @Serializable FROM HERE
@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: String,
    val title: String,
    val streamUrl: String,
    val artistName: String?,  // Flattened from ArtistDto
    val albumTitle: String?,   // Flattened from AlbumDto
    val coverUrl: String?,
    val isLive: Boolean,
    val duration: Int?
)