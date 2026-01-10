package com.girigovardhan.basicmusicplayer.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.girigovardhan.basicmusicplayer.ui.utils.*
import com.girigovardhan.basicmusicplayer.ui.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class MusicPlayer private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: MusicPlayer? = null

        fun getInstance(context: Context): MusicPlayer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicPlayer(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Media components
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val context: Context = context.applicationContext

    // Audio effects
    private var equalizer: Equalizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    // Playback state
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.IDLE)
    val playerState: StateFlow<PlayerState> = _playerState

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _playbackProgress = MutableStateFlow(PlaybackProgress(0L, 0L))
    val playbackProgress: StateFlow<PlaybackProgress> = _playbackProgress

    private val _volume = MutableStateFlow(1.0f) // 0.0 to 1.0
    val volume: StateFlow<Float> = _volume

    // Queue management
    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex

    // Playback modes
    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean> = _isShuffled

    private var originalQueue = listOf<Track>()
    private var shuffledQueue = listOf<Track>()

    // Playback position tracking
    private val progressUpdater = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    init {
        initializePlayer()
    }

    // ==================== INITIALIZATION ====================

    private fun initializePlayer() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener(::onPrepared)
                setOnCompletionListener(::onCompletion)
                setOnErrorListener(::onError)
                setOnBufferingUpdateListener(::onBufferingUpdate)
                setOnSeekCompleteListener(::onSeekComplete)
            }
            _playerState.value = PlayerState.IDLE
            Log.d("MusicPlayer", "Player initialized")
        } catch (e: Exception) {
            _playerState.value = PlayerState.ERROR
            Log.e("MusicPlayer", "Failed to initialize player", e)
        }
    }

    // ==================== PLAYBACK CONTROL ====================

    fun play() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
                _playerState.value = PlayerState.PLAYING
                startProgressUpdates()
                Log.d("MusicPlayer", "Playback started")
            }
        } ?: run {
            initializePlayer()
            loadCurrentTrack()
        }
    }

    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _playerState.value = PlayerState.PAUSED
                stopProgressUpdates()
                Log.d("MusicPlayer", "Playback paused")
            }
        }
    }

    fun stop() {
        mediaPlayer?.let { player ->
            player.stop()
            player.reset()
            _playerState.value = PlayerState.STOPPED
            stopProgressUpdates()
            _playbackProgress.value = PlaybackProgress(0L, 0L)
            Log.d("MusicPlayer", "Playback stopped")
        }
    }

    fun playTrack(track: Track) {
        try {
            _playerState.value = PlayerState.LOADING
            _currentTrack.value = track

            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(context, Uri.parse(track.streamUrl))
            mediaPlayer?.prepareAsync()

            Log.d("MusicPlayer", "Loading track: ${track.title}")
        } catch (e: Exception) {
            _playerState.value = PlayerState.ERROR
            Log.e("MusicPlayer", "Failed to load track", e)
        }
    }

    // ==================== QUEUE MANAGEMENT ====================

    fun setQueue(tracks: List<Track>, startIndex: Int = 0) {
        originalQueue = tracks
        _queue.value = if (_isShuffled.value) {
            shuffledQueue = tracks.shuffled()
            shuffledQueue
        } else {
            tracks
        }

        if (startIndex in tracks.indices) {
            _currentIndex.value = startIndex
            playTrack(tracks[startIndex])
        }

        Log.d("MusicPlayer", "Queue set with ${tracks.size} tracks")
    }

    fun addToQueue(track: Track, playNext: Boolean = false) {
        val currentQueue = _queue.value.toMutableList()
        val insertIndex = if (playNext && _currentIndex.value >= 0) {
            _currentIndex.value + 1
        } else {
            currentQueue.size
        }

        currentQueue.add(insertIndex, track)
        _queue.value = currentQueue

        if (playNext) {
            Log.d("MusicPlayer", "Added ${track.title} to play next")
        } else {
            Log.d("MusicPlayer", "Added ${track.title} to end of queue")
        }
    }

    fun removeFromQueue(index: Int) {
        if (index in _queue.value.indices) {
            val currentQueue = _queue.value.toMutableList()
            val removedTrack = currentQueue.removeAt(index)
            _queue.value = currentQueue

            Log.d("MusicPlayer", "Removed ${removedTrack.title} from queue")
        }
    }

    fun clearQueue() {
        stop()
        _queue.value = emptyList()
        originalQueue = emptyList()
        shuffledQueue = emptyList()
        _currentIndex.value = -1
        _currentTrack.value = null

        Log.d("MusicPlayer", "Queue cleared")
    }

    // ==================== TRACK NAVIGATION ====================

    fun skipToNext() {
        val queue = _queue.value
        val currentIndex = _currentIndex.value

        if (queue.isNotEmpty() && currentIndex >= 0) {
            val nextIndex = when (_repeatMode.value) {
                RepeatMode.ONE -> currentIndex
                else -> {
                    if (currentIndex < queue.size - 1) currentIndex + 1
                    else if (_repeatMode.value == RepeatMode.ALL) 0
                    else -1
                }
            }

            if (nextIndex >= 0 && nextIndex < queue.size) {
                _currentIndex.value = nextIndex
                playTrack(queue[nextIndex])
                Log.d("MusicPlayer", "Skipped to next track at index $nextIndex")
            } else {
                stop()
                Log.d("MusicPlayer", "End of queue reached")
            }
        }
    }

    fun skipToPrevious() {
        val queue = _queue.value
        val currentIndex = _currentIndex.value

        if (queue.isNotEmpty() && currentIndex >= 0) {
            // If less than 3 seconds into the track, go to previous track
            // Otherwise restart current track
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            val shouldSkipTrack = currentPosition < 3000 // 3 seconds

            if (shouldSkipTrack && currentIndex > 0) {
                val prevIndex = currentIndex - 1
                _currentIndex.value = prevIndex
                playTrack(queue[prevIndex])
                Log.d("MusicPlayer", "Skipped to previous track at index $prevIndex")
            } else {
                seekTo(0)
                Log.d("MusicPlayer", "Restarting current track")
            }
        }
    }

    fun skipToIndex(index: Int) {
        val queue = _queue.value
        if (index in queue.indices) {
            _currentIndex.value = index
            playTrack(queue[index])
            Log.d("MusicPlayer", "Skipped to track at index $index")
        }
    }

    // ==================== PLAYBACK MODES ====================

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
        Log.d("MusicPlayer", "Repeat mode set to: $mode")
    }

    fun toggleRepeatMode() {
        val newMode = when (_repeatMode.value) {
            RepeatMode.NONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
        }
        setRepeatMode(newMode)
    }

    fun setShuffle(shuffle: Boolean) {
        _isShuffled.value = shuffle

        if (shuffle) {
            shuffledQueue = originalQueue.shuffled()
            _queue.value = shuffledQueue

            // Update current index for shuffled queue
            val currentTrack = _currentTrack.value
            if (currentTrack != null) {
                val newIndex = shuffledQueue.indexOfFirst { it.id == currentTrack.id }
                _currentIndex.value = newIndex
            }
        } else {
            _queue.value = originalQueue

            // Update current index for original queue
            val currentTrack = _currentTrack.value
            if (currentTrack != null) {
                val newIndex = originalQueue.indexOfFirst { it.id == currentTrack.id }
                _currentIndex.value = newIndex
            }
        }

        Log.d("MusicPlayer", "Shuffle mode: $shuffle")
    }

    fun toggleShuffle() {
        setShuffle(!_isShuffled.value)
    }

    // ==================== PLAYBACK POSITION ====================

    fun seekTo(position: Long) {
        mediaPlayer?.let { player ->
            try {
                player.seekTo(position.toInt())
                updateProgress()
                Log.d("MusicPlayer", "Seeked to position: $position")
            } catch (e: Exception) {
                Log.e("MusicPlayer", "Failed to seek", e)
            }
        }
    }

    fun seekForward(seconds: Int = 10) {
        mediaPlayer?.let { player ->
            val newPosition = (player.currentPosition + seconds * 1000).coerceAtMost(player.duration)
            seekTo(newPosition.toLong())
            Log.d("MusicPlayer", "Seeked forward $seconds seconds")
        }
    }

    fun seekBackward(seconds: Int = 10) {
        mediaPlayer?.let { player ->
            val newPosition = (player.currentPosition - seconds * 1000).coerceAtLeast(0)
            seekTo(newPosition.toLong())
            Log.d("MusicPlayer", "Seeked backward $seconds seconds")
        }
    }

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun getDuration(): Long {
        return mediaPlayer?.duration?.toLong() ?: 0L
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    // ==================== VOLUME CONTROL ====================

    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        mediaPlayer?.setVolume(clampedVolume, clampedVolume)
        _volume.value = clampedVolume
        Log.d("MusicPlayer", "Volume set to: $clampedVolume")
    }

    fun increaseVolume(amount: Float = 0.1f) {
        val newVolume = (_volume.value + amount).coerceAtMost(1.0f)
        setVolume(newVolume)
    }

    fun decreaseVolume(amount: Float = 0.1f) {
        val newVolume = (_volume.value - amount).coerceAtLeast(0.0f)
        setVolume(newVolume)
    }

    fun mute() {
        mediaPlayer?.setVolume(0f, 0f)
        _volume.value = 0f
        Log.d("MusicPlayer", "Muted")
    }

    fun unmute() {
        setVolume(0.5f) // Default volume when unmuting
    }

    fun toggleMute(): Boolean {
        val isMuted = _volume.value == 0f
        if (isMuted) {
            unmute()
        } else {
            mute()
        }
        return !isMuted
    }

    // ==================== AUDIO EFFECTS ====================

    fun enableEqualizer(): Boolean {
        try {
            mediaPlayer?.let { player ->
                equalizer = Equalizer(0, player.audioSessionId).apply {
                    enabled = true
                }
                Log.d("MusicPlayer", "Equalizer enabled")
                return true
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Failed to enable equalizer", e)
        }
        return false
    }

    fun setEqualizerBand(band: Int, level: Short) {
        equalizer?.setBandLevel(band.toShort(), level)
    }

    fun enableLoudnessEnhancer(gain: Int = 1000): Boolean {
        try {
            mediaPlayer?.let { player ->
                loudnessEnhancer = LoudnessEnhancer(player.audioSessionId).apply {
                    setTargetGain(gain)
                    enabled = true
                }
                Log.d("MusicPlayer", "Loudness enhancer enabled with gain: $gain")
                return true
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Failed to enable loudness enhancer", e)
        }
        return false
    }

    fun setPlaybackSpeed(speed: Float): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mediaPlayer?.let { player ->
                player.playbackParams = player.playbackParams.setSpeed(speed)
                true
            } ?: false
        } else {
            false
        }
    }

    // ==================== PLAYBACK INFORMATION ====================

    fun getQueuePosition(): Int {
        return _currentIndex.value
    }

    fun getQueueSize(): Int {
        return _queue.value.size
    }

    fun getRemainingTime(): Long {
        mediaPlayer?.let { player ->
            return (player.duration - player.currentPosition).toLong()
        }
        return 0L
    }

    fun getCurrentTrack(): Track? {
        return _currentTrack.value
    }

    // ==================== PRIVATE METHODS ====================

    private fun onPrepared(mp: MediaPlayer) {
        _playerState.value = PlayerState.PLAYING
        mp.start()
        startProgressUpdates()
        updateProgress()
        Log.d("MusicPlayer", "Track prepared and playing: ${_currentTrack.value?.title}")
    }

    private fun onCompletion(mp: MediaPlayer) {
        _playerState.value = PlayerState.ENDED
        stopProgressUpdates()
        _playbackProgress.value = PlaybackProgress(mp.duration.toLong(), mp.duration.toLong())

        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                seekTo(0)
                play()
            }
            RepeatMode.ALL, RepeatMode.NONE -> {
                handler.postDelayed({
                    skipToNext()
                }, 1000)
            }
        }

        Log.d("MusicPlayer", "Track completed: ${_currentTrack.value?.title}")
    }

    private fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        _playerState.value = PlayerState.ERROR
        stopProgressUpdates()
        Log.e("MusicPlayer", "MediaPlayer error: what=$what, extra=$extra")
        return true
    }

    private fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        if (percent < 100) {
            _playerState.value = PlayerState.BUFFERING
        } else if (!mp.isPlaying) {
            _playerState.value = PlayerState.PLAYING
        }
    }

    private fun onSeekComplete(mp: MediaPlayer) {
        updateProgress()
        Log.d("MusicPlayer", "Seek completed")
    }

    private fun startProgressUpdates() {
        handler.post(progressUpdater)
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(progressUpdater)
    }

    private fun updateProgress() {
        mediaPlayer?.let { player ->
            val currentPos = player.currentPosition.toLong()
            val duration = player.duration.toLong()

            if (duration > 0) {
                _playbackProgress.value = PlaybackProgress(currentPos, duration)
            }
        }
    }

    private fun loadCurrentTrack() {
        val currentTrack = _currentTrack.value
        val currentIndex = _currentIndex.value
        val queue = _queue.value

        if (currentTrack != null) {
            playTrack(currentTrack)
        } else if (currentIndex >= 0 && currentIndex < queue.size) {
            playTrack(queue[currentIndex])
        }
    }

    // ==================== CLEANUP ====================

    fun release() {
        stopProgressUpdates()

        equalizer?.release()
        loudnessEnhancer?.release()

        mediaPlayer?.let { player ->
            player.release()
        }

        mediaPlayer = null
        _playerState.value = PlayerState.IDLE
        _currentTrack.value = null
        _playbackProgress.value = PlaybackProgress(0L, 0L)

        Log.d("MusicPlayer", "Player released")
    }

    fun reset() {
        mediaPlayer?.reset()
        _playerState.value = PlayerState.IDLE
        _currentTrack.value = null
        _playbackProgress.value = PlaybackProgress(0L, 0L)
        stopProgressUpdates()

        Log.d("MusicPlayer", "Player reset")
    }
}