package com.girigovardhan.basicmusicplayer.ui.utils

enum class RepeatMode {
    NONE, ALL, ONE
}

enum class PlayerState {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR,
    BUFFERING,
    ENDED
}

fun formatDuration(duration: Long): String {
    val seconds = duration / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}