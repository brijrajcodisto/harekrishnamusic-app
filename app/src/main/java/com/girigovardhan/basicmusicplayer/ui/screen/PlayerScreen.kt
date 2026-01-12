package com.girigovardhan.basicmusicplayer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModel

@Composable
fun PlayerScreen(viewModel: MusicViewModel) {
    val song = viewModel.currentSong.value
    val isPlaying = viewModel.isPlaying.value

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for Album Art
        Surface(
            modifier = Modifier.size(250.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {}

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = song?.title ?: "Select a Song", style = MaterialTheme.typography.headlineMedium)
        // Text(text = song?.subtitle ?: "Artist", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { viewModel.togglePlayPause() }) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}