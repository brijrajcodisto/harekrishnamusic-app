package com.musicstreaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicstreaming.data.model.Track
import com.musicstreaming.ui.viewmodel.MusicViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.Alignment
import androidx.compose.material3.Slider
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen {
    HOME, PLAYLIST
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicApp()
        }
    }
}

@Composable
fun MusicApp(viewModel: MusicViewModel = koinViewModel()) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.HOME,
                    onClick = { currentScreen = Screen.HOME },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.PLAYLIST,
                    onClick = { currentScreen = Screen.PLAYLIST },
                    icon = { Icon(Icons.Filled.PlaylistAdd, contentDescription = "Playlist") },
                    label = { Text("Playlist") }
                )
            }
        }
    ) { padding ->
        when (currentScreen) {
            Screen.HOME -> HomeScreen(viewModel, modifier = Modifier.padding(padding))
            Screen.PLAYLIST -> PlaylistScreen(viewModel, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun HomeScreen(viewModel: MusicViewModel, modifier: Modifier = Modifier) {
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Update position periodically
    LaunchedEffect(isPlaying) {
        while (true) {
            if (isPlaying) {
                viewModel.updatePosition()
            }
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Music") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.searchTracks(searchQuery) }) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tracks) { track ->
                    TrackItem(track = track, onClick = { viewModel.playTrack(track) }, onAddToPlaylist = { viewModel.addToPlaylist(track) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Playback Controls
        currentTrack?.let { track ->
            Column {
                AsyncImage(
                    model = track.album.cover,
                    contentDescription = "Album Cover",
                    modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
                )
                Text("Now Playing: ${track.title} by ${track.artist.name}", modifier = Modifier.align(Alignment.CenterHorizontally))
                if (duration > 0) {
                    Slider(
                        value = currentPosition.toFloat(),
                        onValueChange = { viewModel.seekTo(it.toLong()) },
                        valueRange = 0f..duration.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("${formatTime(currentPosition)} / ${formatTime(duration)}", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { viewModel.playTrack(track) }) {
                        Text("Play")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.pauseTrack() }) {
                        Text("Pause")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.stopTrack() }) {
                        Text("Stop")
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistScreen(viewModel: MusicViewModel, modifier: Modifier = Modifier) {
    val playlist by viewModel.playlist.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Playlist", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        if (playlist.isEmpty()) {
            Text("No tracks in playlist", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(playlist) { track ->
                    PlaylistItem(track = track, onPlay = { viewModel.playTrack(track) }, onRemove = { viewModel.removeFromPlaylist(track) })
                }
            }
            Button(onClick = { viewModel.playPlaylist() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Play Playlist")
            }
        }
    }
}

@Composable
fun TrackItem(track: Track, onClick: () -> Unit, onAddToPlaylist: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), onClick = onClick) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = track.album.cover,
                contentDescription = "Album Cover",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, style = MaterialTheme.typography.headlineSmall)
                Text(track.artist.name, style = MaterialTheme.typography.bodyMedium)
                Text(track.album.title, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onAddToPlaylist) {
                Icon(Icons.Filled.PlaylistAdd, contentDescription = "Add to Playlist")
            }
        }
    }
}

@Composable
fun PlaylistItem(track: Track, onPlay: () -> Unit, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = track.album.cover,
                contentDescription = "Album Cover",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, style = MaterialTheme.typography.headlineSmall)
                Text(track.artist.name, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onPlay) {
                Text("Play")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
