package com.huydd2908.demomusic.data.source

import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.data.source.local.OnDataLoadListener

class SongRepository private constructor(private val local: SongDataSource) : SongDataSource {
    override fun getSongs(listener: OnDataLoadListener<MutableList<Song>>) {
        local.getSongs(listener)
    }

    companion object {
        private var instance: SongRepository? = null
        fun getInstance(local: SongDataSource) = instance ?: SongRepository(local).also {
            instance = it
        }
    }
}
