package com.musicstreaming.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.musicstreaming.data.local.entity.TrackEntity
import com.musicstreaming.data.local.entity.UserEntity
import com.musicstreaming.data.local.dao.TrackDao
import com.musicstreaming.data.local.dao.UserDao

@Database(
    entities = [TrackEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_streaming_db"
                )
                    .build()
                    .also { instance = it }
            }
    }
}
