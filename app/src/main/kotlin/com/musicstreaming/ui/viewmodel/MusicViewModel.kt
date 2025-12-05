package com.musicstreaming.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import com.musicstreaming.data.model.Track
import com.musicstreaming.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel(
    private val repository: MusicRepository,
    application: Application
) : AndroidViewModel(application) {

    private val exoPlayer = ExoPlayer.Builder(application).build()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _playlist = MutableStateFlow<List<Track>>(emptyList())
    val playlist: StateFlow<List<Track>> = _playlist

    init {
        exoPlayer.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _isPlaying.value = playbackState == androidx.media3.common.Player.STATE_READY && exoPlayer.isPlaying
            }

            override fun onPositionDiscontinuity(
                oldPosition: androidx.media3.common.Player.PositionInfo,
                newPosition: androidx.media3.common.Player.PositionInfo,
                reason: Int
            ) {
                updatePosition()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    fun updatePosition() {
        _currentPosition.value = exoPlayer.currentPosition
        _duration.value = exoPlayer.duration
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.searchTracks(query)
                _tracks.value = result
            } catch (e: Exception) {
                _tracks.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun playTrack(track: Track) {
        val mediaItem = MediaItem.fromUri(track.preview)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        _currentTrack.value = track
        _isPlaying.value = true
        updatePosition()
    }

    fun pauseTrack() {
        exoPlayer.pause()
        _isPlaying.value = false
    }

    fun stopTrack() {
        exoPlayer.stop()
        _isPlaying.value = false
        _currentPosition.value = 0
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun addToPlaylist(track: Track) {
        val current = _playlist.value.toMutableList()
        if (!current.contains(track)) {
            current.add(track)
            _playlist.value = current
        }
    }

    fun removeFromPlaylist(track: Track) {
        val current = _playlist.value.toMutableList()
        current.remove(track)
        _playlist.value = current
    }

    fun playPlaylist() {
        val playlist = _playlist.value
        if (playlist.isNotEmpty()) {
            playTrack(playlist.first())
        }
    }

    override fun onCleared() {
        exoPlayer.release()
        super.onCleared()
    }
}