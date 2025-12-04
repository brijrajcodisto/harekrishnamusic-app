package com.musicstreaming.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstreaming.data.model.Track
import com.musicstreaming.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(private val musicRepository: MusicRepository) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    init {
        loadTrendingTracks()
    }

    fun loadTrendingTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.getTrendingTracks().collect { result ->
                result.onSuccess { tracks ->
                    _tracks.value = tracks
                    _error.value = null
                }.onFailure { e ->
                    _error.value = e.message
                }
                _isLoading.value = false
            }
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            musicRepository.searchTracks(query).collect { result ->
                result.onSuccess { searchResult ->
                    _searchResults.value = searchResult.tracks
                    _error.value = null
                }.onFailure { e ->
                    _error.value = e.message
                }
                _isLoading.value = false
            }
        }
    }

    fun selectTrack(track: Track) {
        _currentTrack.value = track
    }

    fun clearError() {
        _error.value = null
    }
}
