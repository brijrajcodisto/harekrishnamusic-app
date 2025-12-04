package com.musicstreaming.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.musicstreaming.ui.screen.LoginScreen
import com.musicstreaming.ui.screen.SignupScreen
import com.musicstreaming.ui.viewmodel.AuthViewModel
import com.musicstreaming.ui.viewmodel.MusicViewModel
import com.musicstreaming.ui.viewmodel.YouTubeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainActivity() {
    val authViewModel = koinViewModel<AuthViewModel>()
    val musicViewModel = koinViewModel<MusicViewModel>()
    val youtubeViewModel = koinViewModel<YouTubeViewModel>()
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        MainNavigation(
            authViewModel = authViewModel,
            musicViewModel = musicViewModel,
            youtubeViewModel = youtubeViewModel,
            navController = navController
        )
    }
}

@Composable
fun MainNavigation(
    authViewModel: AuthViewModel,
    musicViewModel: MusicViewModel,
    youtubeViewModel: YouTubeViewModel,
    navController: NavHostController
) {
    var currentRoute by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == "home",
                    onClick = {
                        currentRoute = "home"
                        navController.navigate("home")
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = currentRoute == "search",
                    onClick = {
                        currentRoute = "search"
                        navController.navigate("search")
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "YouTube") },
                    label = { Text("YouTube") },
                    selected = currentRoute == "youtube",
                    onClick = {
                        currentRoute = "youtube"
                        navController.navigate("youtube")
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                // Music list screen
                Text("Home Screen")
            }
            composable("search") {
                // Search screen
                Text("Search Screen")
            }
            composable("youtube") {
                // YouTube screen
                Text("YouTube Screen")
            }
        }
    }
}
