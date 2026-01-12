package com.girigovardhan.basicmusicplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModel

@Composable
fun MiniPlayer(
    viewModel: MusicViewModel,
    onClick: () -> Unit
) {
    val currentSong = viewModel.currentSong.value ?: return
    val isPlaying = viewModel.isPlaying.value

    // Get time values from VM
    val position = viewModel.currentPosition.value
    val duration = viewModel.totalDuration.value
    val sliderPosition = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {

            // 1. Interactive Seekbar
            Slider(
                value = sliderPosition,
                onValueChange = { viewModel.seekTo(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            // 2. Time Labels Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatTime(position), style = MaterialTheme.typography.labelSmall)
                Text(text = formatTime(duration), style = MaterialTheme.typography.labelSmall)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ... (Album Art and Song Info same as before)

                AsyncImage(
                    model = currentSong.coverUrl,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = currentSong.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    // Text(text = currentSong.subtitle, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }

                IconButton(onClick = { viewModel.togglePlayPause() }) {
                    Text(if (isPlaying) "⏸" else "▶", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

private fun RowScope.formatTime(ms: Long): String {
    // Media3 duration can return a negative value if the stream isn't ready
    if (ms < 0) return "00:00"

    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    // Returns a string like "04:20"
    return String.format("%02d:%02d", minutes, seconds)
}
