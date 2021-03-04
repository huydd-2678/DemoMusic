package com.huydd2908.demomusic.data.source

import com.huydd2908.demomusic.data.model.Song

class SongRepository private constructor(private val local: SongDataSource.Local) {
    fun getSongs(): MutableList<Song> = local.getSongs()

    companion object {
        private var instance: SongRepository? = null
        fun getInstance(local: SongDataSource.Local) = instance ?: SongRepository(local).also {
            instance = it
        }
    }
}
