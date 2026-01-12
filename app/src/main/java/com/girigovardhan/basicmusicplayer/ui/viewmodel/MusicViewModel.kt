package com.girigovardhan.basicmusicplayer.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.girigovardhan.basicmusicplayer.playback.service.MusicService
import com.girigovardhan.basicmusicplayer.data.model.Song
import com.google.common.util.concurrent.MoreExecutors
import androidx.lifecycle.viewModelScope
import com.girigovardhan.basicmusicplayer.data.repository.MusicRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MusicViewModel : ViewModel() {

    // 1. Initialize repository here
    private val repository = MusicRepository()

    private val _songs = mutableStateOf<List<Song>>(emptyList())
    val songs: State<List<Song>> = _songs
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private var controller: MediaController? = null
    var isPlaying = mutableStateOf(false)
    var currentSong = mutableStateOf<Song?>(null)

    private val _progress = mutableStateOf(0f)
    val progress: State<Float> = _progress

    private val _currentPosition = mutableStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _totalDuration = mutableStateOf(0L)
    val totalDuration: State<Long> = _totalDuration

    init {
        // Fetch songs automatically when ViewModel is created
        loadSongs()
    }

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
            startProgressUpdate()
        }, MoreExecutors.directExecutor())
    }

    fun playSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(song.streamUrl)
            .build()

        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        controller?.play()
        currentSong.value = song
    }

    fun togglePlayPause() {
        if (isPlaying.value) controller?.pause() else controller?.play()
    }

    private fun startProgressUpdate() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = controller?.currentPosition ?: 0L
                _totalDuration.value = controller?.duration ?: 0L
                delay(1000)
            }
        }
    }

    fun loadSongs() {
        // 1. Create a coroutine scope
        viewModelScope.launch {
            try {
                // 2. Now you can safely call the suspend function
                val result = repository.getSongs()
                _songs.value = result
            } catch (e: Exception) {
                // 3. Handle network errors here
                _songs.value = emptyList()
            }
        }
    }

    fun seekTo(position: Float) {
        // Converts slider percentage (0.0 - 1.0) back to milliseconds
        val seekMs = (position * _totalDuration.value).toLong()
        controller?.seekTo(seekMs)
    }

}