# Music Streaming App

A comprehensive Kotlin-based Android music streaming application with support for local music APIs, YouTube integration, user authentication, and live streaming.

## Features

### 1. Music Streaming from API
- Stream music from external music APIs
- Browse trending tracks
- Support for multiple audio formats
- Media playback using ExoPlayer

### 2. Guest Access
- Play music without authentication
- Limited features but full music playback support
- Enjoy core music experience without account creation

### 3. YouTube Integration
- Search for YouTube videos
- Stream YouTube content
- Play YouTube live streams
- Integrated YouTube player support

### 4. Search Functionality
- Full-text search for:
  - Tracks
  - Artists
  - Playlists
- Filter and sort results

### 5. User Authentication
- Sign Up: Create new account with email/password
- Login: Secure login with encrypted token storage
- Session management
- Automatic token refresh

### 6. Live Streaming
- YouTube live streaming support
- Live stream discovery
- Real-time viewer integration

## Project Structure

```
harekrishnamusic-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/musicstreaming/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/          # Retrofit interfaces
│   │   │   │   │   ├── local/        # Room database
│   │   │   │   │   ├── model/        # Data models
│   │   │   │   │   ├── repository/   # Data repositories
│   │   │   │   │   └── auth/         # Authentication
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/       # Compose screens
│   │   │   │   │   ├── viewmodel/    # MVVM ViewModels
│   │   │   │   │   └── MainActivity
│   │   │   │   ├── service/          # Music playback service
│   │   │   │   └── MusicStreamingApp # Application class
│   │   │   ├── res/
│   │   │   │   ├── values/           # Strings, themes
│   │   │   │   └── xml/              # Preferences
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Dependencies

### Core Android
- AndroidX Core, AppCompat, Material3
- Jetpack Compose for UI

### Networking
- Retrofit 2 for API calls
- OkHttp for HTTP client
- Gson for JSON serialization

### Database
- Room for local data persistence
- DataStore for preferences

### Media
- Media3 ExoPlayer for music playback
- Media3 Session for media controls

### YouTube
- android-youtube-player library

### Dependency Injection
- Koin for dependency injection

### Security
- EncryptedSharedPreferences for secure token storage

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 26 or higher
- Kotlin 1.9.0+
- Gradle 8.1+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/harekrishnamusic-app.git
   cd harekrishnamusic-app
   ```

2. **Open in Android Studio**
   - File → Open → Select project folder

3. **Configure API Keys**
   - Add music API credentials in `local.properties`
   - Configure YouTube API key in `AndroidManifest.xml`

   ```properties
   MUSIC_API_KEY=your_api_key
   YOUTUBE_API_KEY=your_youtube_key
   ```

4. **Sync Gradle**
   - Android Studio will automatically sync dependencies

5. **Build and Run**
   - Click "Run" or press `Shift + F10`

## API Integration

### Music API Configuration

Update the base URL in `MusicStreamingApp.kt`:

```kotlin
.baseUrl("https://your-music-api.com/")
```

### YouTube API Setup

1. Get API key from [Google Cloud Console](https://console.cloud.google.com/)
2. Enable YouTube Data API v3
3. Add to `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.youtube.api_key"
    android:value="YOUR_API_KEY" />
```

## Architecture

### MVVM Pattern
- **Model**: Data classes and repositories
- **View**: Jetpack Compose UI screens
- **ViewModel**: State management and business logic

### Clean Architecture
- Separation of concerns
- Data, domain, and presentation layers
- Repository pattern for data access

### Dependency Injection
- Koin for managing dependencies
- Singleton instances for services

## Usage Examples

### Authentication
```kotlin
// Sign up
authViewModel.signup("username", "email@example.com", "password")

// Login
authViewModel.login("email@example.com", "password")

// Guest access
authViewModel.continueAsGuest()
```

### Music Playback
```kotlin
// Search for music
musicViewModel.searchTracks("Hare Krishna")

// Select and play track
musicViewModel.selectTrack(track)
```

### YouTube
```kotlin
// Search videos
youtubeViewModel.searchVideos("Hare Krishna Music")

// Load live streams
youtubeViewModel.loadLiveStreams()
```

## Permissions

The app requires the following permissions:
- `INTERNET` - For API calls and streaming
- `ACCESS_NETWORK_STATE` - To check network connectivity
- `READ_EXTERNAL_STORAGE` - To access local files
- `WRITE_EXTERNAL_STORAGE` - To cache content

## Features Roadmap

- [ ] Local playlist management
- [ ] Music recommendations based on listening history
- [ ] Offline mode with caching
- [ ] Audio equalizer
- [ ] Social sharing
- [ ] Lyrics display
- [ ] Family sharing
- [ ] Premium subscriptions

## Testing

Run tests with:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Build APK

Generate release APK:
```bash
./gradlew assembleRelease
```

Find the APK in `app/build/outputs/apk/release/`

## Troubleshooting

### API Call Failures
- Verify internet connectivity
- Check API credentials in `local.properties`
- Ensure base URL is correct in `MusicStreamingApp.kt`

### YouTube Integration Issues
- Confirm YouTube API key is valid
- Check YouTube API is enabled in Google Cloud Console
- Verify AndroidManifest metadata

### Authentication Problems
- Clear app data and try again
- Check encrypted SharedPreferences setup
- Verify server endpoint is reachable

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see LICENSE.md for details

## Support

For support, email support@harekrishnamusic.com or open an issue on GitHub

## Acknowledgments

- Built with Kotlin and Jetpack Compose
- Uses ExoPlayer for media playback
- YouTube integration via android-youtube-player
- Inspired by modern music streaming applications
