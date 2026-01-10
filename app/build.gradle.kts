
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt) // This MUST be present
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.girigovardhan.basicmusicplayer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.girigovardhan.basicmusicplayer"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.hilt.android)

    implementation("io.coil-kt.coil3:coil:3.3.0")

    // Optional: Add for network image loading (required in Coil 3.x)
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

//    implementation(libs.coil.compose) // Check for the latest version
//    implementation(libs.hilt.android)


    implementation("androidx.appcompat:appcompat:1.6.1")

    // Constraint Layout
    // implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ExoPlayer for audio playback
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Room Database (optional)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp(libs.hilt.compiler)
    // kapt("com.google.dagger:hilt-android-compiler:2.48")

    // kapt "androidx.room:room-compiler:2.6.1"

    // Material Design
    implementation("com.google.android.material:material:1.11.0")
// ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")
// CardView
    implementation("androidx.cardview:cardview:1.0.0")
// ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
// RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // For Lottie animations
    implementation("com.airbnb.android:lottie:6.1.0")
// For circular progress
    implementation("com.github.zagum:Android-SwitchIcon:1.4.0")
// For smooth animations
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    val fragment_version = "1.8.0" // Check for latest 2026 version
    implementation("androidx.appcompat:appcompat:1.7.0")

    val nav_version = "2.7.7" // Or the latest version
    implementation("androidx.navigation:navigation-fragment-ktx:${nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${nav_version}")
    // Feature module support for Fragments
    implementation("androidx.fragment:fragment-ktx:1.6.2")
}