package com.huydd2908.demomusic.ui.song

import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.data.source.SongRepository
import com.huydd2908.demomusic.data.source.local.OnDataLoadListener
import com.huydd2908.demomusic.ui.song.SongContract.*

class SongPresenter(
    private val repository: SongRepository,
    private val view: View
) : Presenter {

    override fun onStart() {
        getSongs()
    }

    override fun getSongs() {
        repository.getSongs(object : OnDataLoadListener<MutableList<Song>> {
            override fun onSuccess(data: MutableList<Song>) {
                view.showSongs(data)
            }

            override fun onFail(message: String) {
                view.showError(message)
            }
        })
    }
}
