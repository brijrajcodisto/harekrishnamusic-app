package com.musicstreaming.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val imageUrl: String?,
    val previewUrl: String?,
    val streamUrl: String?,
    val isLiked: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val profileImage: String?,
    val accessToken: String,
    val refreshToken: String?,
    val createdAt: Long = System.currentTimeMillis()
)
