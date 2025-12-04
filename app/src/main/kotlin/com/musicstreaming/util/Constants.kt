package com.musicstreaming.util

/**
 * Music streaming constants and configuration
 */
object MusicStreamingConstants {
    // API Configuration
    const val MUSIC_API_BASE_URL = "https://api.example.com/"
    const val YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3/"

    // Audio Quality
    enum class AudioQuality(val bitrate: Int) {
        LOW(128),
        MEDIUM(192),
        HIGH(320),
        LOSSLESS(1411)
    }

    // Error Messages
    object ErrorMessages {
        const val NETWORK_ERROR = "Network error. Please check your connection."
        const val API_ERROR = "API error. Please try again later."
        const val AUTH_ERROR = "Authentication failed. Please login again."
        const val PLAYBACK_ERROR = "Playback error. Please try another track."
    }

    // Shared Preferences Keys
    object PreferencesKeys {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_ID = "user_id"
        const val AUDIO_QUALITY = "audio_quality"
        const val OFFLINE_MODE = "offline_mode"
    }

    // Database
    const val DATABASE_NAME = "music_streaming_db"
    const val DATABASE_VERSION = 1

    // Timeouts
    const val NETWORK_TIMEOUT_SECONDS = 30L
    const val PLAYBACK_BUFFER_SIZE = 512 * 1024 // 512KB
}
