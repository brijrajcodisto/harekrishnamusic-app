package com.girigovardhan.basicmusicplayer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.girigovardhan.basicmusicplayer.data.repository.MusicRepository
import com.girigovardhan.basicmusicplayer.ui.components.HomeTopBar
import com.girigovardhan.basicmusicplayer.ui.components.SongItem
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onSongClick: () -> Unit
) {
    val songs = viewModel.songs.value
    val isLoading = viewModel.isLoading.value
    Scaffold(
        topBar = {
            HomeTopBar(
                onSearchClick = {
                    // Handle search click (e.g., navigate to search screen)
                    android.util.Log.d("Navigation", "Search Clicked")
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            items(songs) { song ->
                SongItem(
                    song = song,
                    onClick = {
                        viewModel.playSong(it)
                        onSongClick()
                    }
                )
            }
        }
    }
}