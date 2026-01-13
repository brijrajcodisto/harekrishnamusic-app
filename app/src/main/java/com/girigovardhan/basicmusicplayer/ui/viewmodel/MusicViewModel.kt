package com.girigovardhan.sravanam.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.girigovardhan.sravanam.playback.service.MusicService
import com.girigovardhan.sravanam.data.model.Song
import com.google.common.util.concurrent.MoreExecutors
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.girigovardhan.sravanam.data.repository.MusicRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import androidx.paging.PagingConfig

class MusicViewModel(private val repository: MusicRepository) : ViewModel() {

    private val _allSongs = mutableStateOf<List<Song>>(emptyList())

    // 1. Track the search query as a Flow
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val isSyncing = repository.isSyncing

    // 2. State for Loading UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    @OptIn( FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pagedSongs: Flow<PagingData<Song>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,          // Chunks of 20 songs
                    prefetchDistance = 5,   // Start loading next page when 5 items from bottom
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    if (query.isBlank()) repository.getAllSongsPaged()
                    else repository.searchSongsPaged(query)
                }
            ).flow
        }
        .cachedIn(viewModelScope) // Important: keeps the scroll position during configuration changes

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class) // Add this here
    val songs: StateFlow<List<Song>> = _searchQuery
        .debounce(300) // Wait 300ms after typing stops to save battery/CPU
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allSongs // Show all songs if search is empty
            } else {
                repository.searchSongs(query) // Show filtered songs
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var controller: MediaController? = null
    var isPlaying = mutableStateOf(false)
    var currentSong = mutableStateOf<Song?>(null)

    private val _progress = mutableStateOf(0f)
    val progress: State<Float> = _progress

    private val _currentPosition = mutableStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _totalDuration = mutableStateOf(0L)
    val totalDuration: State<Long> = _totalDuration

    init {
        // Fetch songs automatically when ViewModel is created
        refreshData()
    }

    fun initController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying.value = playing
                }
            })
            startProgressUpdate()
        }, MoreExecutors.directExecutor())
    }

    fun playSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(song.streamUrl)
            .build()

        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        controller?.play()
        currentSong.value = song
    }

    fun togglePlayPause() {
        if (isPlaying.value) controller?.pause() else controller?.play()
    }

    private fun startProgressUpdate() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = controller?.currentPosition ?: 0L
                _totalDuration.value = controller?.duration ?: 0L
                delay(1000)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshSongs() // Fetches from Ktor and saves to Room
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _isLoading.value = true
        _searchQuery.value = newQuery
        _isLoading.value = false
    }

    fun seekTo(position: Float) {
        // Converts slider percentage (0.0 - 1.0) back to milliseconds
        val seekMs = (position * _totalDuration.value).toLong()
        controller?.seekTo(seekMs)
    }

}