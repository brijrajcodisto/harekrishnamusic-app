package com.girigovardhan.basicmusicplayer.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.girigovardhan.basicmusicplayer.databinding.FragmentPlayerBinding
import com.girigovardhan.basicmusicplayer.ui.models.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.girigovardhan.basicmusicplayer.ui.utils.*
import com.girigovardhan.basicmusicplayer.R
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModels()

    private var isSeeking = false
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            updateHandler.postDelayed(this, 1000) // Update every second
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        // Initial UI state
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        binding.songProgress.setOnSeekBarChangeListener(createSeekBarListener())

        // Add album art rotation animation
        startAlbumRotation()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.currentTrack.collectLatest { track ->
                track?.let {
                    updateTrackInfo(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.playerState.collectLatest { state ->
                updatePlaybackState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.playbackProgress.collectLatest { progress ->
                if (!isSeeking) {
                    binding.songProgress.progress = progress.currentPosition.toInt()
                    binding.currentTime.text = formatDuration(progress.currentPosition)
                    binding.totalTime.text = formatDuration(progress.duration)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnPlayPause.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.btnNext.setOnClickListener {
            viewModel.skipToNext()
        }

        binding.btnPrevious.setOnClickListener {
            viewModel.skipToPrevious()
        }

        binding.btnRepeat.setOnClickListener {
            viewModel.toggleRepeatMode()
        }

        binding.btnShuffle.setOnClickListener {
            viewModel.toggleShuffle()
        }

        binding.btnAddToPlaylist.setOnClickListener {
            // Navigate to queue/playlist
            // (navigation logic would go here)
        }

        binding.btnMore.setOnClickListener {
            // Show more options menu
            showMoreOptions()
        }

        binding.albumArt.setOnClickListener {
            // Toggle fullscreen album art if needed
        }
    }

    private fun updateTrackInfo(track: Track) {
        binding.apply {
            songTitle.text = track.title
            artistName.text = track.artist
            albumName.text = track.album

            // Load album art using Coil
//            albumArt.load(track.albumArtUrl) {
//                crossfade(true)
//                placeholder(R.drawable.ic_album_placeholder)
//                error(R.drawable.ic_album_error)
//            }

            // Update shuffle and repeat button states
            updateRepeatButton(viewModel.repeatMode)
            btnShuffle.setImageResource(
                if (viewModel.isShuffled.value) R.drawable.ic_shuffle_on
                else R.drawable.ic_shuffle_off
            )
        }
    }

    private fun updatePlaybackState(state: PlayerState) {
        when (state) {
            PlayerState.PLAYING -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                startAlbumRotation()
                updateHandler.post(updateRunnable)
            }
            PlayerState.PAUSED -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                pauseAlbumRotation()
                updateHandler.removeCallbacks(updateRunnable)
            }
            PlayerState.IDLE -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                resetAlbumRotation()
                updateHandler.removeCallbacks(updateRunnable)
            }
            PlayerState.LOADING -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_loading)
                binding.songProgress.isVisible = true
            }
            PlayerState.ERROR -> {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                showErrorState()
            }

            PlayerState.STOPPED -> TODO()
            PlayerState.BUFFERING -> TODO()
            PlayerState.ENDED -> TODO()
        }
        binding.songProgress.isVisible = state == PlayerState.LOADING
    }

    private fun updateProgress() {
        if (!isSeeking) {
            val currentPos = viewModel.getCurrentPosition()
            val duration = viewModel.getDuration()

            binding.songProgress.max = duration.toInt()
            binding.songProgress.progress = currentPos.toInt()

            binding.currentTime.text = formatDuration(currentPos)
            binding.totalTime.text = formatDuration(duration)
        }
    }

    private fun createSeekBarListener() = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                binding.currentTime.text = formatDuration(progress.toLong())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            isSeeking = true
            updateHandler.removeCallbacks(updateRunnable)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            isSeeking = false
            viewModel.seekTo(seekBar.progress.toLong())

            if (viewModel.playerState.value == PlayerState.PLAYING) {
                updateHandler.post(updateRunnable)
            }
        }
    }

    private fun startAlbumRotation() {
        binding.albumArt.animate().cancel()
        binding.albumArt.animate()
            .rotationBy(360f)
            .setDuration(20000) // 20 seconds per rotation
            .setInterpolator(LinearInterpolator())
            .withEndAction {
                if (viewModel.playerState.value == PlayerState.PLAYING) {
                    startAlbumRotation() // Continue rotation
                }
            }
            .start()
    }

    private fun pauseAlbumRotation() {
        binding.albumArt.animate().cancel()
    }

    private fun resetAlbumRotation() {
        binding.albumArt.animate().cancel()
        binding.albumArt.rotation = 0f
    }

    private fun updateRepeatButton(mode: StateFlow<RepeatMode>) {
        val iconRes = when (mode.value) {
            RepeatMode.NONE -> R.drawable.ic_repeat_off
            RepeatMode.ALL -> R.drawable.ic_repeat_on
            RepeatMode.ONE -> R.drawable.ic_repeat_one
        }
        binding.btnRepeat.setImageResource(iconRes)
    }

    private fun showMoreOptions() {
        // Implement bottom sheet dialog with options:
        // - Add to playlist
        // - View album
        // - View artist
        // - Share
        // - Sleep timer
        // - Equalizer
        // - Lyrics
    }

    private fun showErrorState() {
        // Show error message or retry button
        binding.songTitle.text = getString(R.string.error_playing_track)
        binding.btnPlayPause.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateHandler.removeCallbacks(updateRunnable)
        _binding = null
    }

    companion object {
        fun newInstance() = PlayerFragment()
    }
}