package com.girigovardhan.basicmusicplayer.data.repository

import com.girigovardhan.basicmusicplayer.data.model.Song

class MusicRepository {
    fun getSongs(): List<Song> {
        return listOf(
            Song(
                id = "1",
                title = "Nature Sounds",
                subtitle = "Ambient Relax",
                mediaUrl = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3",
                iconUrl = "https://picsum.photos/200"
            ),
            Song(
                id = "2",
                title = "Jazz Beats",
                subtitle = "Smooth Radio",
                mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                iconUrl = "https://picsum.photos/201"
            )
        )
    }
}