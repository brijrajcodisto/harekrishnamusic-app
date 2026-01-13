package com.girigovardhan.basicmusicplayer.data.local

import android.content.Context

class MusicPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_VERSION = "last_version"
    }

    fun getLocalVersion(): String {
        return prefs.getString(KEY_LAST_VERSION, "") ?: ""
    }

    fun saveLocalVersion(version: String) {
        prefs.edit().putString(KEY_LAST_VERSION, version).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}