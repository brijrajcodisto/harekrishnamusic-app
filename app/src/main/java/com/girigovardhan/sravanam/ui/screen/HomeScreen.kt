package com.girigovardhan.sravanam.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.girigovardhan.sravanam.data.repository.MusicRepository
import com.girigovardhan.sravanam.ui.components.HomeTopBar
import com.girigovardhan.sravanam.ui.components.SongItem
import com.girigovardhan.sravanam.ui.viewmodel.MusicViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey // Also useful for LazyColumn keys

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onSongClick: () -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val pagedSongs = viewModel.pagedSongs.collectAsLazyPagingItems()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (songs.isEmpty() && !isLoading) {
                // Empty State
                EmptyLibraryContent(searchQuery)
            } else {
                // List of Songs
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(songs, key = { it.id }) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                viewModel.playSong(song)
                                onSongClick()
                            }
                        )
                    }
                }
            }

            // Loading Overlay
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun EmptyLibraryContent(query: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = if (query.isEmpty()) "No songs in your library" else "No results for \"$query\"",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}