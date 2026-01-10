package com.girigovardhan.basicmusicplayer.ui.models

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtUrl: String,
    val duration: Long,
    val streamUrl: String,
    val isLiked: Boolean = false,
    val isExplicit: Boolean = false,
    val year: Int? = null,
    val genre: String? = null,
    val trackNumber: Int? = null,
    val bitrate: Int? = null // in kbps
)

data class PlaybackProgress(
    val currentPosition: Long,
    val duration: Long
) {
    val progressPercentage: Float
        get() = if (duration > 0) (currentPosition.toFloat() / duration) else 0f

    val remainingTime: Long
        get() = duration - currentPosition
}