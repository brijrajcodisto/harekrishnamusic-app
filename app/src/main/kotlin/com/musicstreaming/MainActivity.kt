package com.musicstreaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.musicstreaming.ui.MainActivity
import com.musicstreaming.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicStreamingAppUI()
        }
    }
}

@Composable
fun MusicStreamingAppUI() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val authViewModel = koinViewModel<AuthViewModel>()
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

            if (isLoggedIn) {
                MainActivity()
            } else {
                // Show login/signup screens
                AuthenticationFlow(authViewModel)
            }
        }
    }
}

@Composable
fun AuthenticationFlow(authViewModel: AuthViewModel) {
    // Placeholder - integrate with actual auth screens
    println("Authentication flow - show login/signup")
}
