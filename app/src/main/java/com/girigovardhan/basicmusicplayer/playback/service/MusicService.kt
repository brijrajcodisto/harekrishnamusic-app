package com.girigovardhan.basicmusicplayer.playback.service

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        // 1. Define the type of audio
        val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
            .setUsage(androidx.media3.common.C.USAGE_MEDIA)
            .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        // 2. Build the player with these attributes
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true) // true = handle audio focus automatically
            .setHandleAudioBecomingNoisy(true) // pauses if headphones are unplugged
            .build()
        // val player = ExoPlayer.Builder(this).build()
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                // This will print the exact reason for no sound in your Logcat
                android.util.Log.e("MusicService", "Player Error: ${error.message}")
            }
        })
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        // This ensures that any controller (including Bluetooth) has permission
        // to call play, pause, seek, etc.
        return mediaSession
    }

    // Add this to handle command permissions explicitly
    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        // This helps Media3 manage the foreground state better for the OS
        super.onUpdateNotification(session, startInForegroundRequired)
    }
}