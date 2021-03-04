package com.huydd2908.demomusic.ui.song

import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.utils.BasePresenter

interface SongContract {
    interface View {
        fun showSongs(songs: MutableList<Song>)
    }

    interface Presenter : BasePresenter<View> {
        fun getSongs()
    }
}
