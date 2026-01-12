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
                val navController = rememberNavController()

                // Scaffold provides the structure for top bars and bottom bars
                Scaffold(
                    bottomBar = {
                        MiniPlayer(
                            viewModel = viewModel,
                            onClick = { navController.navigate("player") }
                        )
                    }
                ) { innerPadding ->
                    // Apply innerPadding so the NavHost content isn't hidden by the MiniPlayer
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(viewModel = viewModel, onSongClick = {
                                // We don't necessarily have to navigate to player anymore
                                // because the MiniPlayer will appear!
                            })
                        }
                        composable("player") {
                            PlayerScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}