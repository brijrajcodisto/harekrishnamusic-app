package com.girigovardhan.basicmusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.girigovardhan.basicmusicplayer.ui.components.MiniPlayer
import com.girigovardhan.basicmusicplayer.ui.screen.HomeScreen
import com.girigovardhan.basicmusicplayer.ui.screen.PlayerScreen
import com.girigovardhan.basicmusicplayer.ui.theme.BasicMusicPlayerTheme
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initController(this)

        setContent {
            BasicMusicPlayerTheme {
                // 1. Create the driver
                val navController = rememberNavController()

                // 2. Observe the current song for the MiniPlayer
                val currentSong = viewModel.currentSong.value

                Scaffold(
                    bottomBar = {
                        // Show MiniPlayer only if a song is loaded
                        if (currentSong != null) {
                            MiniPlayer(
                                viewModel = viewModel,
                                onClick = {
                                    // Use the driver to change the screen
                                    navController.navigate("player")
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    // 3. Define the Container (NavHost)
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Route: Home Screen
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onSongClick = {
                                    navController.navigate("player")
                                }
                            )
                        }

                        // Route: Full Player Screen
                        composable("player") {
                            PlayerScreen(
                                viewModel = viewModel,
                                onBackClick = {
                                    // Navigate back to Home
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}