package com.huydd2908.demomusic.ui.song

import android.util.Log
import com.huydd2908.demomusic.data.source.SongRepository
import com.huydd2908.demomusic.ui.song.SongContract.*

class SongPresenter(private val repository: SongRepository) : Presenter {
    private var view: View? = null

    override fun onStart() {
        getSongs()
    }

    override fun setView(view: View?) {
        this.view = view
    }

    override fun getSongs() {
        view?.showSongs(repository.getSongs())
        Log.e("getSongs", repository.getSongs().toString())
    }
}
