# Quick Reference Guide

## Building the App

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Run on connected device
./gradlew installDebug

# Clean build
./gradlew clean build
```

## Key Files to Modify for Integration

### 1. Music API Configuration
**File**: `app/src/main/kotlin/com/musicstreaming/MusicStreamingApp.kt`

Change:
```kotlin
.baseUrl("https://api.example.com/")
```

To your actual music API base URL.

### 2. API Endpoints
**File**: `app/src/main/kotlin/com/musicstreaming/data/api/ApiInterfaces.kt`

Add endpoints matching your music API:
```kotlin
@GET("/your/endpoint")
suspend fun methodName(): ResponseType
```

### 3. YouTube API Key
**File**: `local.properties` (create from `local.properties.example`)

Add:
```properties
YOUTUBE_API_KEY=your_key_here
```

### 4. Authentication
**File**: `app/src/main/kotlin/com/musicstreaming/ui/viewmodel/AuthViewModel.kt`

Replace API call simulations with real backend calls:
```kotlin
// Replace this simulation with actual API call
val userId = authApi.signup(username, email, password)
```

## Running Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific test
./gradlew test --tests com.musicstreaming.data.repository.*
```

## Gradle Commands Reference

```bash
# List all tasks
./gradlew tasks

# Sync gradle files
./gradlew --refresh-dependencies

# Check dependencies
./gradlew dependencies

# Show dependency tree
./gradlew dependencies --configuration debugRuntimeClasspath

# Update all dependencies (careful with this)
./gradlew dependencyUpdates
```

## Common Issues & Solutions

### Issue: Build Fails with Gradle Error
```bash
# Solution: Clean and rebuild
./gradlew clean build
```

### Issue: "Cannot find symbol" errors
```bash
# Solution: Sync gradle
./gradlew --refresh-dependencies
```

### Issue: Emulator runs slowly
```bash
# Solution: Use USB connected device or increase emulator RAM
# In AVD settings, increase RAM to 2-4GB
```

### Issue: API calls returning 401 Unauthorized
```kotlin
// Check token is being included in headers
// Verify token hasn't expired
// Check authentication flow in AuthManager
```

## Debug Mode

### Enable Network Logging
Modify `MusicStreamingApp.kt`:
```kotlin
.addInterceptor(LoggingInterceptor())  // Already included
```

### View Database Content
Connect device and run:
```bash
adb shell
sqlite3 /data/data/com.musicstreaming/databases/music_streaming_db
```

### Check Logs
```bash
# View all logs
./gradlew logcat

# Filter logs
./gradlew logcat | grep musicstreaming
```

## Performance Profiling

In Android Studio:
1. Run → Profile 'app'
2. Select profiler:
   - **CPU Profiler**: Check method execution time
   - **Memory Profiler**: Detect memory leaks
   - **Network Profiler**: Monitor API calls
   - **Energy Profiler**: Battery usage

## Debugging Tips

### Add Breakpoints
1. Click on line number in code editor
2. Run → Debug 'app'
3. Use Step Over (F6), Step Into (F7), Step Out (Shift+F8)

### View Variable Values
Hover over variables when paused at breakpoint

### Conditional Breakpoints
Right-click on breakpoint → Edit → Add condition

### Logcat Filtering
```bash
adb logcat | grep -i "musicstreaming\|error\|exception"
```

## Useful ADB Commands

```bash
# Install app
adb install app/build/outputs/apk/debug/app-debug.apk

# Clear app data
adb shell pm clear com.musicstreaming

# List installed apps
adb shell pm list packages | grep musicstreaming

# Pull database
adb pull /data/data/com.musicstreaming/databases/music_streaming_db

# Push file to device
adb push file.txt /sdcard/
```

## Code Style Guidelines

### Kotlin Naming Conventions
```kotlin
// Classes: PascalCase
class MusicViewModel { }

// Functions/Variables: camelCase
fun getCurrentTrack() { }
val isPlaying: Boolean = false

// Constants: UPPER_SNAKE_CASE
const val DATABASE_NAME = "music_db"

// Private members: prefix with _
private val _tracks = MutableStateFlow()
val tracks = _tracks.asStateFlow()
```

### Function Organization
1. Public properties
2. Private properties
3. Lifecycle methods
4. Public methods
5. Private methods

## Resources & Documentation

- [Android Developers](https://developer.android.com)
- [Kotlin Documentation](https://kotlinlang.org/docs)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Retrofit Guide](https://square.github.io/retrofit)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [ExoPlayer](https://exoplayer.dev)
- [Koin DI](https://insert-koin.io)
- [YouTube API](https://developers.google.com/youtube)

## Useful Android Studio Shortcuts (Mac)

- `Cmd + ,` - Preferences
- `Cmd + B` - Go to definition
- `Cmd + U` - Go to super method
- `Cmd + Alt + O` - Organize imports
- `Cmd + Shift + F` - Find in files
- `Cmd + Shift + R` - Replace in files
- `Cmd + Shift + A` - Find actions
- `Cmd + J` - Insert live template
- `Cmd + /` - Comment/uncomment

## Firebase Integration (Optional)

Add to `build.gradle.kts`:
```kotlin
implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
```

## Release Checklist

- [ ] Update version in `build.gradle.kts`
- [ ] Test on multiple devices/Android versions
- [ ] Check for lint errors: `./gradlew lint`
- [ ] Run all tests: `./gradlew test`
- [ ] Update documentation
- [ ] Create signed APK: `./gradlew assembleRelease`
- [ ] Test signed APK on device
- [ ] Create GitHub release
- [ ] Upload to Play Store (if applicable)
