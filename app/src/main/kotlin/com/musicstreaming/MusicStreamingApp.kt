package com.musicstreaming

import android.app.Application
import android.content.Context
import com.musicstreaming.data.api.MusicApi
import com.musicstreaming.data.auth.AuthManager
import com.musicstreaming.data.local.AppDatabase
import com.musicstreaming.data.repository.MusicRepository
import com.musicstreaming.ui.viewmodel.AuthViewModel
import com.musicstreaming.ui.viewmodel.MusicViewModel
import com.musicstreaming.ui.viewmodel.YouTubeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MusicStreamingApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MusicStreamingApp)
            modules(appModule)
        }
    }

    companion object {
        private val appModule = module {
            // Database
            single {
                AppDatabase.getInstance(get<Context>())
            }

            single {
                get<AppDatabase>().trackDao()
            }

            single {
                get<AppDatabase>().userDao()
            }

            // Auth Manager
            single {
                AuthManager(get<Context>())
            }

            // Networking
            single {
                Retrofit.Builder()
                    .baseUrl("https://api.example.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            single {
                get<Retrofit>().create(MusicApi::class.java)
            }

            // Repository
            single {
                MusicRepository(get<MusicApi>())
            }

            // ViewModels
            viewModel {
                MusicViewModel(get<MusicRepository>())
            }

            viewModel {
                AuthViewModel(get<AuthManager>(), get())
            }

            viewModel {
                YouTubeViewModel()
            }
        }
    }
}
