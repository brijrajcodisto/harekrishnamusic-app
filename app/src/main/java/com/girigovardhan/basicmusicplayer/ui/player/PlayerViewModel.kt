package com.girigovardhan.basicmusicplayer.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import com.girigovardhan.basicmusicplayer.ui.models.*
import com.girigovardhan.basicmusicplayer.ui.utils.*
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {

    private val musicPlayer = MusicPlayer.getInstance(context)

    // Expose player state
    val currentTrack: StateFlow<Track?> = musicPlayer.currentTrack
    val playerState: StateFlow<PlayerState> = musicPlayer.playerState
    val playbackProgress: StateFlow<PlaybackProgress> = musicPlayer.playbackProgress
    val queue: StateFlow<List<Track>> = musicPlayer.queue
    val currentIndex: StateFlow<Int> = musicPlayer.currentIndex
    val repeatMode: StateFlow<RepeatMode> = musicPlayer.repeatMode
    val isShuffled: StateFlow<Boolean> = musicPlayer.isShuffled
    val volume: StateFlow<Float> = musicPlayer.volume

    // Basic playback controls
    fun togglePlayback() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause()
        } else {
            musicPlayer.play()
        }
    }

    fun play() = musicPlayer.play()
    fun pause() = musicPlayer.pause()
    fun stop() = musicPlayer.stop()

    // Track navigation
    fun skipToNext() = musicPlayer.skipToNext()
    fun skipToPrevious() = musicPlayer.skipToPrevious()
    fun skipToIndex(index: Int) = musicPlayer.skipToIndex(index)

    // Seek operations
    fun seekTo(position: Long) = musicPlayer.seekTo(position)
    fun seekForward(seconds: Int = 10) = musicPlayer.seekForward(seconds)
    fun seekBackward(seconds: Int = 10) = musicPlayer.seekBackward(seconds)

    // Playback info
    fun getCurrentPosition(): Long = musicPlayer.getCurrentPosition()
    fun getDuration(): Long = musicPlayer.getDuration()
    fun isPlaying(): Boolean = musicPlayer.isPlaying()

    // Queue management
    fun setQueue(tracks: List<Track>, startIndex: Int = 0) =
        musicPlayer.setQueue(tracks, startIndex)

    fun addToQueue(track: Track, playNext: Boolean = false) =
        musicPlayer.addToQueue(track, playNext)

    fun removeFromQueue(index: Int) = musicPlayer.removeFromQueue(index)
    fun clearQueue() = musicPlayer.clearQueue()

    // Playback modes
    fun setRepeatMode(mode: RepeatMode) = musicPlayer.setRepeatMode(mode)
    fun toggleRepeatMode() = musicPlayer.toggleRepeatMode()
    fun setShuffle(shuffle: Boolean) = musicPlayer.setShuffle(shuffle)
    fun toggleShuffle() = musicPlayer.toggleShuffle()

    // Volume control
    fun setVolume(volume: Float) = musicPlayer.setVolume(volume)
    fun increaseVolume(amount: Float = 0.1f) = musicPlayer.increaseVolume(amount)
    fun decreaseVolume(amount: Float = 0.1f) = musicPlayer.decreaseVolume(amount)
    fun mute() = musicPlayer.mute()
    fun unmute() = musicPlayer.unmute()
    fun toggleMute(): Boolean = musicPlayer.toggleMute()

    // Audio effects
    fun enableEqualizer(): Boolean = musicPlayer.enableEqualizer()
    fun setEqualizerBand(band: Int, level: Short) = musicPlayer.setEqualizerBand(band, level)
    fun enableLoudnessEnhancer(gain: Int = 1000): Boolean = musicPlayer.enableLoudnessEnhancer(gain)
    fun setPlaybackSpeed(speed: Float): Boolean = musicPlayer.setPlaybackSpeed(speed)

    // Playback information
    fun getQueuePosition(): Int = musicPlayer.getQueuePosition()
    fun getQueueSize(): Int = musicPlayer.getQueueSize()
    fun getRemainingTime(): Long = musicPlayer.getRemainingTime()
    fun getCurrentTrack(): Track? = musicPlayer.getCurrentTrack()

    // Cleanup
    override fun onCleared() {
        super.onCleared()
        // Note: Don't release here as it's a singleton
        // The player will be released when the app closes
    }
}