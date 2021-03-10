package com.huydd2908.demomusic.ui.song

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huydd2908.demomusic.R
import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.data.source.SongRepository
import com.huydd2908.demomusic.data.source.local.SongLocalDataSource
import com.huydd2908.demomusic.service.SongNotificationControl
import com.huydd2908.demomusic.service.SongService
import com.huydd2908.demomusic.ui.song.adapter.SongAdapter
import kotlinx.android.synthetic.main.activity_song.*
import java.util.*

class SongActivity : AppCompatActivity(),
    SongContract.View,
    SeekBar.OnSeekBarChangeListener {

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val delay: Long = 0
    private val period: Long = 1000
    private var songAdapter = SongAdapter(itemClick = { song -> songClick(song) })
    private var songService: SongService? = null
    private var songs = mutableListOf<Song>()
    private val connection = object : ServiceConnection, SongNotificationControl {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SongService.SongBinder
            songService = binder.getService()
            songService?.setSongs(songs)
            songService?.setCallback(this)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            songService?.stop()
        }

        override fun onChange() {
            showSongInfo()
        }

        override fun onPlayPause() {
            buttonControlPlayPause.apply {
                if (songService?.isPlaying() == true) {
                    setImageResource(R.drawable.ic_pause_black_36dp)
                } else {
                    setImageResource(R.drawable.ic_play_arrow_black_36dp)
                }
            }
        }
    }

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

    override fun showSongs(songs: MutableList<Song>) {
        this.songs = songs
        bindService()
        songAdapter.updateData(songs)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            songService?.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
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
        groupControl.visibility = View.GONE
        recyclerViewSong.adapter = songAdapter
        recyclerViewSong.setHasFixedSize(true)
        seekBar.setOnSeekBarChangeListener(this)
        buttonControlNext.setOnClickListener {
            songService?.change(1)
            showSongInfo()
        }
    }

    private fun initData() {
        val presenter =
            SongPresenter(
                SongRepository.getInstance(SongLocalDataSource.getInstance(contentResolver)),
                this
            )
        presenter.onStart()
    }

    private fun bindService() {
        val intent = Intent(this, SongService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    private fun songClick(song: Song) {
        songService?.create(songs.indexOf(song))
        groupControl.visibility = View.VISIBLE
        showSongInfo()
    }

    private fun showSongInfo() {
        textControlTitle.text = songService?.getTitle()
        textControlArtist.text = songService?.getArtist()
        seekBar.max = songService?.getDuration()!!
        updateSeekBar()
        buttonControlPlayPause.apply {
            setOnClickListener {
                if (songService?.isPlaying() == true) {
                    songService?.pause()
                    this.setImageResource(R.drawable.ic_play_arrow_black_36dp)
                } else {
                    songService?.start()
                    this.setImageResource(R.drawable.ic_pause_black_36dp)
                }
            }
        }
    }

    private fun updateSeekBar() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (songService?.mediaPlayer != null) {
                        seekBar.post {
                            seekBar.progress = songService?.getCurrentPosition()!!
                        }
                    } else {
                        timer.cancel()
                        timer.purge()
                    }
                }
            }
        }, delay, period)
    }

    override fun onDestroy() {
        super.onDestroy()
        groupControl.visibility = View.GONE
        unbindService(connection)
    }
}
