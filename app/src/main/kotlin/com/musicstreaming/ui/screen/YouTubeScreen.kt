package com.musicstreaming.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicstreaming.data.model.YouTubeVideo
import com.musicstreaming.ui.viewmodel.YouTubeViewModel

@Composable
fun YouTubeScreen(
    viewModel: YouTubeViewModel,
    onVideoSelected: (YouTubeVideo) -> Unit
) {
    val videos by viewModel.videos.collectAsState()
    val liveStreams by viewModel.liveStreams.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("YouTube Videos & Live Streams", modifier = Modifier.padding(bottom = 16.dp))

        Button(
            onClick = { viewModel.loadLiveStreams() },
            enabled = !isLoading
        ) {
            Text("Load Live Streams")
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        if (liveStreams.isNotEmpty()) {
            Text("Live Streams", modifier = Modifier.padding(top = 16.dp))
            liveStreams.forEach { video ->
                YouTubeVideoItem(
                    video = video,
                    onVideoClick = { onVideoSelected(video) }
                )
            }
        }

        if (videos.isNotEmpty()) {
            Text("Videos", modifier = Modifier.padding(top = 16.dp))
            videos.forEach { video ->
                YouTubeVideoItem(
                    video = video,
                    onVideoClick = { onVideoSelected(video) }
                )
            }
        }
    }
}

@Composable
fun YouTubeVideoItem(
    video: YouTubeVideo,
    onVideoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(video.title)
        Text(video.channelTitle)
        if (video.isLiveContent) {
            Text("🔴 LIVE", modifier = Modifier.padding(top = 4.dp))
        }
        Button(
            onClick = onVideoClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Watch")
        }
    }
}
