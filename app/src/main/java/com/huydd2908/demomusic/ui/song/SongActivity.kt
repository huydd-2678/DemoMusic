package com.huydd2908.demomusic.ui.song

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.huydd2908.demomusic.R
import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.data.source.SongRepository
import com.huydd2908.demomusic.data.source.local.SongLocalDataSource
import com.huydd2908.demomusic.service.SongService
import com.huydd2908.demomusic.ui.song.adapter.SongAdapter
import com.huydd2908.demomusic.utils.OnRecyclerViewItemClickListener
import kotlinx.android.synthetic.main.activity_song.*

class SongActivity : AppCompatActivity(), OnRecyclerViewItemClickListener<Song>, SongContract.View {
    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val songAdapter: SongAdapter by lazy { SongAdapter() }
    private var songService: SongService? = null
    private var songs = mutableListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)
        when {
            checkPermission() -> {
                bindService()
                initView()
                initData()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                requestPermissions(permissions, 0)
            }
            else -> {
                finish()
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (p in permissions) {
                val status = checkSelfPermission(p)
                if (status == PackageManager.PERMISSION_DENIED) {
                    return false
                }
            }
        }
        return true
    }

    private fun bindService() {
        val intent = Intent(this, SongService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SongService.SongBinder
            songService = binder.getService()
            songService?.setSongs(songs)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    private fun initView() {
        recyclerViewSong.adapter = songAdapter
        recyclerViewSong.setHasFixedSize(true)
        songAdapter.setListener(this)
    }

    private fun initData() {
        val presenter =
            SongPresenter(SongRepository.getInstance(SongLocalDataSource.getInstance(this)))
        presenter.setView(this)
        presenter.onStart()
    }

    override fun showSongs(songs: MutableList<Song>) {
        this.songs = songs
        songAdapter.updateData(songs)
    }

    override fun onItemClickListener(item: Song) {
        songService?.create(songs.indexOf(item))
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}
