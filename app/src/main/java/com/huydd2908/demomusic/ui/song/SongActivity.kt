package com.huydd2908.demomusic.ui.song

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.huydd2908.demomusic.R
import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.data.source.SongRepository
import com.huydd2908.demomusic.data.source.local.SongLocalDataSource
import com.huydd2908.demomusic.ui.song.adapter.SongAdapter
import com.huydd2908.demomusic.utils.OnRecyclerViewItemClickListener
import kotlinx.android.synthetic.main.activity_song.*

class SongActivity : AppCompatActivity(), OnRecyclerViewItemClickListener<Song>, SongContract.View {
    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val songAdapter: SongAdapter by lazy { SongAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)
        when {
            checkPermission() -> {
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
        songAdapter.updateData(songs)
        Log.e("showSongs", songs.size.toString())
    }

    override fun onItemClickListener(item: Song) {
        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
    }
}
