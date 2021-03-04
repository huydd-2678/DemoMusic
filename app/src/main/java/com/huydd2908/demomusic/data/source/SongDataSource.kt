package com.huydd2908.demomusic.data.source

import com.huydd2908.demomusic.data.model.Song

interface SongDataSource {
    interface Local {
        fun getSongs(): MutableList<Song>
    }
}
