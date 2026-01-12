package com.girigovardhan.basicmusicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModel

@Composable
fun MiniPlayer(
    viewModel: MusicViewModel,
    onClick: () -> Unit
) {
    val currentSong = viewModel.currentSong.value ?: return // Don't show if nothing is playing
    val isPlaying = viewModel.isPlaying.value

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = currentSong.title, style = MaterialTheme.typography.titleMedium)
                Text(text = currentSong.subtitle, style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = { viewModel.togglePlayPause() }) {
                // Using a simple text or icon toggle
                Text(if (isPlaying) "⏸" else "▶")
            }
        }
    }
}