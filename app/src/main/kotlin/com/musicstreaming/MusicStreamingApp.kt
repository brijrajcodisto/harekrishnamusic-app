package com.musicstreaming

import android.app.Application
import com.musicstreaming.data.api.MusicApi
import com.musicstreaming.data.repository.MusicRepository
import com.musicstreaming.ui.viewmodel.MusicViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.androidx.viewmodel.dsl.viewModel
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

    private val appModule = module {
        single {
            androidContext()
        }

        single {
            Retrofit.Builder()
                .baseUrl("https://api.deezer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single {
            get<Retrofit>().create(MusicApi::class.java)
        }

        single {
            MusicRepository(get())
        }

        viewModel {
            MusicViewModel(get(), get())
        }
    }
}
