package com.girigovardhan.basicmusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.girigovardhan.basicmusicplayer.data.local.MusicDatabase
import com.girigovardhan.basicmusicplayer.data.repository.MusicRepository
import com.girigovardhan.basicmusicplayer.ui.components.MiniPlayer
import com.girigovardhan.basicmusicplayer.ui.screen.HomeScreen
import com.girigovardhan.basicmusicplayer.ui.screen.PlayerScreen
import com.girigovardhan.basicmusicplayer.ui.theme.BasicMusicPlayerTheme
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModel
import com.girigovardhan.basicmusicplayer.ui.viewmodel.MusicViewModelFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Create the Database using the Activity Context (Safely)
        val database = MusicDatabase.getDatabase(applicationContext)
        val songDao = database.songDao()

        // 2. Create the Network Client
        val client = HttpClient(Android) {
            // This plugin handles converting JSON to your Song objects automatically
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Prevents crashing if API sends extra data
                    prettyPrint = true
                    isLenient = true
                })
            }

            defaultRequest {
                header("X-API-Key", BuildConfig.MUSIC_API_KEY) // BuildConfig.MUSIC_API_KEY
                header("X-API-Secret", BuildConfig.MUSIC_API_SECRET) // BuildConfig.MUSIC_API_SECRET
                // Optional: If your API also requires Content-Type
                header("Content-Type", "application/json")
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        android.util.Log.d("KtorClient", message)
                    }
                }
            }
        }

        // 3. Create the Repository
        val repository = MusicRepository(songDao, client)

        // 4. Create the ViewModel via the Factory
        val viewModel: MusicViewModel by viewModels {
            MusicViewModelFactory(repository)
        }

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