package com.girigovardhan.basicmusicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // Declare MediaPlayer for audio playback
    private lateinit var mediaPlayer: MediaPlayer

    // Declare UI elements
    private lateinit var seekBar: SeekBar
    private lateinit var textCurrentTime: TextView
    private lateinit var textTotalTime: TextView
    private lateinit var buttonPlay: ImageView
    private lateinit var buttonPause: ImageView
    private lateinit var buttonStop: ImageView

    // Handler to update SeekBar and current time text every second
    private val handler = Handler(Looper.getMainLooper())

    // Runnable task that updates SeekBar and current playback time
    private val updateSeekBar: Runnable = object : Runnable {
        override fun run() {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {

                // Update SeekBar progress and current time text
                seekBar.progress = mediaPlayer.currentPosition
                textCurrentTime.text = formatTime(mediaPlayer.currentPosition)

                // Repeat this task every 1 second
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_old)

        // Initialize views from layout
        seekBar = findViewById(R.id.seekBar)
        textCurrentTime = findViewById(R.id.textCurrentTime)
        textTotalTime = findViewById(R.id.textTotalTime)
        buttonPlay = findViewById(R.id.buttonPlay)
        buttonPause = findViewById(R.id.buttonPause)
        buttonStop = findViewById(R.id.buttonStop)

        // Create MediaPlayer instance with a raw audio resource
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)

        // Set listener to configure SeekBar and total time after MediaPlayer is ready
        mediaPlayer.setOnPreparedListener {
            seekBar.max = it.duration
            textTotalTime.text = formatTime(it.duration)
        }

        // Play button starts the audio and begins updating UI
        buttonPlay.setOnClickListener {
            mediaPlayer.start()
            handler.post(updateSeekBar)
        }

        // Pause button pauses the audio playback
        buttonPause.setOnClickListener {
            mediaPlayer.pause()
        }

        // Stop button stops playback and resets UI and MediaPlayer
        buttonStop.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer = MediaPlayer.create(this, R.raw.sound)
            seekBar.progress = 0
            textCurrentTime.text = "0:00"
            textTotalTime.text = formatTime(mediaPlayer.duration)
        }

        // Listen for SeekBar user interaction
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            // Called when progress is changed
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Seek MediaPlayer to new position and update current time
                    mediaPlayer.seekTo(progress)
                    textCurrentTime.text = formatTime(progress)
                }
            }

            // Not used, but required to override
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            // Not used, but required to override
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Format milliseconds into minutes:seconds format (e.g., 1:05)
    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    // Clean up MediaPlayer and handler when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBar)
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}