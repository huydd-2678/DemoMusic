package com.huydd2908.demomusic.data.source

import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.data.source.local.OnDataLoadListener

interface SongDataSource {
    fun getSongs(listener: OnDataLoadListener<MutableList<Song>>)
}
