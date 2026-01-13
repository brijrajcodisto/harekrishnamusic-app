package com.girigovardhan.basicmusicplayer.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModel

//@Composable
//fun PlayerScreen(viewModel: MusicViewModel) {
//    val song = viewModel.currentSong.value
//    val isPlaying = viewModel.isPlaying.value
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        // Placeholder for Album Art
//        Surface(
//            modifier = Modifier.size(250.dp),
//            color = MaterialTheme.colorScheme.primaryContainer,
//            shape = MaterialTheme.shapes.medium
//        ) {}
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Text(text = song?.title ?: "Select a Song", style = MaterialTheme.typography.headlineMedium)
//        // Text(text = song?.subtitle ?: "Artist", style = MaterialTheme.typography.bodyLarge)
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(onClick = { viewModel.togglePlayPause() }) {
//            Text(if (isPlaying) "Pause" else "Play")
//        }
//    }
//}
//

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: MusicViewModel,
    onBackClick: () -> Unit
) {
    val song = viewModel.currentSong.value
    val isPlaying = viewModel.isPlaying.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Now Playing",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown, // Or Icons.Default.ArrowBack
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Spacer to keep the title centered if using TopAppBar
                    // (Not needed if using CenterAlignedTopAppBar)
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Often looks better for player screens
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
}