package com.musicstreaming.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstreaming.data.model.YouTubeVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class YouTubeViewModel : ViewModel() {

    private val _videos = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val videos: StateFlow<List<YouTubeVideo>> = _videos.asStateFlow()

    private val _liveStreams = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val liveStreams: StateFlow<List<YouTubeVideo>> = _liveStreams.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedVideo = MutableStateFlow<YouTubeVideo?>(null)
    val selectedVideo: StateFlow<YouTubeVideo?> = _selectedVideo.asStateFlow()

    fun searchVideos(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Integrate with YouTube API
                val mockVideos = listOf(
                    YouTubeVideo(
                        id = "video1",
                        title = query,
                        channelTitle = "Sample Channel",
                        thumbnailUrl = "",
                        isLiveContent = false
                    )
                )
                _videos.value = mockVideos
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadLiveStreams() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Integrate with YouTube API
                _liveStreams.value = emptyList()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectVideo(video: YouTubeVideo) {
        _selectedVideo.value = video
    }
}
