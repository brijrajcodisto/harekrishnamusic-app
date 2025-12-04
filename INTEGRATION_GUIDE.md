# Integration Guide for Hare Krishna Music App

This guide provides step-by-step instructions for integrating real APIs and services into your music streaming application.

## 1. Music API Integration

### Supported Music APIs:
- **Spotify**: For music streaming
- **Last.fm**: For track information
- **SoundCloud**: For independent artist content
- **Custom API**: Your own music backend

### Example: Using a Custom Music API

**Step 1**: Update the Retrofit client configuration in `MusicStreamingApp.kt`

```kotlin
single {
    Retrofit.Builder()
        .baseUrl("https://your-music-api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addInterceptor(AuthInterceptor(get()))
        .addInterceptor(LoggingInterceptor())
        .build()
}
```

**Step 2**: Update API endpoints in `ApiInterfaces.kt`

```kotlin
interface MusicApi {
    @GET("/api/v1/tracks/trending")
    suspend fun getTrendingTracks(): List<Track>

    @GET("/api/v1/search")
    suspend fun searchTracks(@Query("q") query: String): SearchResult
}
```

**Step 3**: Configure authentication headers with your API tokens

In `local.properties` (create if it doesn't exist):
```properties
MUSIC_API_KEY=your_api_key_here
MUSIC_API_SECRET=your_api_secret_here
```

### Example: Spotify Integration

```kotlin
// In MusicStreamingApp.kt
single {
    val spotifyApi = SpotifyAPI.Builder()
        .setClientID("YOUR_SPOTIFY_CLIENT_ID")
        .setRedirectURI("your-app-scheme://callback")
        .showDialog(true)
        .build()
}
```

## 2. YouTube Integration

### Prerequisites:
1. Google Cloud Project created
2. YouTube Data API v3 enabled
3. OAuth 2.0 credentials generated

### Setup Steps:

**Step 1**: Get YouTube API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create new project or select existing
3. Enable YouTube Data API v3
4. Create OAuth 2.0 credentials (Create credentials → OAuth 2.0 Client ID)
5. Add Android app details

**Step 2**: Add API key to project

In `local.properties`:
```properties
YOUTUBE_API_KEY=your_youtube_api_key
```

In `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.youtube.api_key"
    android:value="YOUR_YOUTUBE_API_KEY" />
```

**Step 3**: Implement YouTube API client

Update `YouTubeViewModel.kt`:

```kotlin
fun searchVideos(query: String) {
    viewModelScope.launch {
        _isLoading.value = true
        try {
            val results = youtubeApi.searchVideos(
                q = query,
                part = "snippet",
                type = "video",
                maxResults = 50
            )
            _videos.value = results.items.map { item ->
                YouTubeVideo(
                    id = item.id.videoId,
                    title = item.snippet.title,
                    channelTitle = item.snippet.channelTitle,
                    thumbnailUrl = item.snippet.thumbnails.default.url,
                    isLiveContent = false
                )
            }
        } catch (e: Exception) {
            // Handle error
        } finally {
            _isLoading.value = false
        }
    }
}
```

## 3. Authentication Setup

### Using Firebase Authentication:

**Step 1**: Add Firebase dependencies to `build.gradle.kts`

```kotlin
implementation("com.google.firebase:firebase-auth-ktx:22.0.0")
implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")
```

**Step 2**: Update `AuthViewModel.kt` with Firebase

```kotlin
fun login(email: String, password: String) {
    viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            authManager.saveToken(user?.uid ?: "")
            authManager.saveUserId(user?.uid ?: "")
            
            _isLoggedIn.value = true
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
}
```

### Using Custom Backend:

Update API interface:
```kotlin
@POST("/auth/login")
suspend fun login(@Body request: LoginRequest): AuthResponse

@POST("/auth/signup")
suspend fun signup(@Body request: SignupRequest): AuthResponse

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
```

## 4. Live Streaming Integration

### YouTube Live Streams:

```kotlin
@GET("/youtube/live")
suspend fun getLiveStreams(
    @Query("forMine") forMine: Boolean = true
): List<YouTubeVideo>
```

### Custom RTMP Live Streaming:

Use ExoPlayer with RTMP support:
```kotlin
val uri = Uri.parse("rtmp://your-rtmp-server.com/live/stream")
val mediaItem = MediaItem.Builder()
    .setUri(uri)
    .build()

player.setMediaItem(mediaItem)
player.prepare()
player.play()
```

## 5. Local Database Setup

The app uses Room database. Initialize with initial data:

```kotlin
fun populateInitialData() {
    viewModelScope.launch {
        try {
            val tracks = musicRepository.getTrendingTracks().first()
            tracks.forEach { track ->
                trackDao.insertTrack(track.toEntity())
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

## 6. Testing APIs

### Manual Testing:

Use cURL or Postman:

```bash
# Get trending tracks
curl -X GET "https://your-music-api.com/api/tracks" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Search tracks
curl -X GET "https://your-music-api.com/api/search?q=hare%20krishna" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Unit Testing:

```kotlin
class MusicRepositoryTest {
    private val mockApi = mockk<MusicApi>()
    private val repository = MusicRepository(mockApi)

    @Test
    fun testSearchTracks() = runBlocking {
        val mockResults = SearchResult(
            tracks = listOf(mockTrack),
            artists = emptyList(),
            playlists = emptyList()
        )
        coEvery { mockApi.searchTracks("test") } returns mockResults

        repository.searchTracks("test").collect { result ->
            assert(result.isSuccess)
        }
    }
}
```

## 7. Deployment Checklist

- [ ] All API endpoints configured
- [ ] API keys stored securely (not in code)
- [ ] YouTube API enabled and configured
- [ ] Authentication flow tested
- [ ] Database migrations verified
- [ ] Error handling implemented
- [ ] Logging configured
- [ ] Performance optimized
- [ ] Security review completed
- [ ] Build release APK

## 8. Environment Configuration

Create `config.properties` for managing different environments:

```properties
# Development
DEV_API_URL=https://dev-api.example.com
DEV_YOUTUBE_KEY=dev_key

# Production
PROD_API_URL=https://api.example.com
PROD_YOUTUBE_KEY=prod_key
```

Load in code:
```kotlin
val buildType = if (BuildConfig.DEBUG) "DEV" else "PROD"
val apiUrl = BuildConfig.getString("${buildType}_API_URL")
```

## Troubleshooting

### API Call Failures
- Check network connectivity
- Verify API keys are correct
- Check API rate limits
- Review server logs

### Authentication Issues
- Clear SharedPreferences data
- Verify token expiration
- Check refresh token implementation
- Ensure secure storage of tokens

### YouTube Integration Problems
- Verify YouTube API is enabled
- Check OAuth credentials
- Ensure proper redirect URIs
- Test with YouTube Data API Explorer

## Support

For additional help:
- Read Android documentation: https://developer.android.com
- YouTube API docs: https://developers.google.com/youtube
- Retrofit guide: https://square.github.io/retrofit/
- Jetpack Compose: https://developer.android.com/jetpack/compose
