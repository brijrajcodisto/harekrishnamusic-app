package com.girigovardhan.basicmusicplayer.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.girigovardhan.basicmusicplayer.playback.service.MusicService
import com.girigovardhan.basicmusicplayer.data.model.Song
import com.google.common.util.concurrent.MoreExecutors

class MusicViewModel : ViewModel() {
    private var controller: MediaController? = null
    var isPlaying = mutableStateOf(false)
    var currentSong = mutableStateOf<Song?>(null)

    fun initController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying.value = playing
                }
            })
        }, MoreExecutors.directExecutor())
    }

    fun playSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(song.mediaUrl)
            .build()

        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        controller?.play()
        currentSong.value = song
    }

    fun togglePlayPause() {
        if (isPlaying.value) controller?.pause() else controller?.play()
    }
}