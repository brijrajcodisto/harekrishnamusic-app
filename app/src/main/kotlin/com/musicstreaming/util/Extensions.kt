package com.musicstreaming.util

import com.musicstreaming.data.model.Track
import com.musicstreaming.data.local.entity.TrackEntity

/**
 * Extension functions and mappers for data transformation
 */

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        imageUrl = imageUrl,
        previewUrl = previewUrl,
        streamUrl = streamUrl
    )
}

fun TrackEntity.toModel(): Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        imageUrl = imageUrl,
        previewUrl = previewUrl,
        streamUrl = streamUrl
    )
}

fun Long.toTimeString(): String {
    val seconds = (this / 1000) % 60
    val minutes = (this / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
}

fun String.isStrongPassword(): Boolean {
    // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character
    return this.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"))
}
