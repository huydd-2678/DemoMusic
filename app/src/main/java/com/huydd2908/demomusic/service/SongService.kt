package com.huydd2908.demomusic.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.huydd2908.demomusic.R
import com.huydd2908.demomusic.data.model.Song

class SongService : Service(), MediaPlayer.OnCompletionListener {
    private var remoteViews: RemoteViews? = null
    var mediaPlayer: MediaPlayer? = null
    private var songs = mutableListOf<Song>()
    private var index = 0
    private var songNotificationControl: SongNotificationControl? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_CLOSE)
        intentFilter.addAction(ACTION_PREVIOUS)
        intentFilter.addAction(ACTION_PLAY)
        intentFilter.addAction(ACTION_NEXT)
        registerReceiver(receiver, intentFilter)
    }

    override fun onBind(intent: Intent?): IBinder = SongBinder(this)

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return START_NOT_STICKY
//    }

    class SongBinder(private val service: SongService) : Binder() {
        fun getService(): SongService = service
    }

    fun setSongs(songs: MutableList<Song>) {
        if (mediaPlayer != null) {
            release()
        }
        this.songs = songs
    }

    fun setCallback(songControl: SongNotificationControl) {
        this.songNotificationControl = songControl
    }

    private fun createRemoteViews() {
        remoteViews = RemoteViews(packageName, R.layout.notification_song)
        registerAction(ACTION_NEXT, R.id.buttonNext)
        registerAction(ACTION_PREVIOUS, R.id.buttonPrevious)
        registerAction(ACTION_PLAY, R.id.buttonPlayPause)
        registerAction(ACTION_CLOSE, R.id.buttonClose)
        remoteViews?.setTextViewText(R.id.textNotificationSongTitle, getTitle())
        remoteViews?.setImageViewResource(
            R.id.buttonPlayPause,
            if (isPlaying()) R.drawable.ic_pause_black_36dp else R.drawable.ic_play_arrow_black_36dp
        )
    }

    private fun registerAction(action: String, id: Int) {
        val intent = Intent(action)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        remoteViews?.setOnClickPendingIntent(id, pendingIntent)
    }

    private fun createNotification() {
        val intent = Intent(this, javaClass)
        startService(intent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        createRemoteViews()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.apply {
            setSmallIcon(R.drawable.ic_song)
            setCustomBigContentView(remoteViews)
        }
        startForeground(1, builder.build())
    }

    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_CLOSE -> {
                    release()
                    mediaPlayer = null
                    stopForeground(true)
                    stopSelf()
                }
                ACTION_PLAY -> {
                    if (isPlaying()) {
                        pause()
                    } else {
                        start()
                    }
                    songNotificationControl?.onPlayPause()
                }
                ACTION_NEXT -> {
                    change(1)
                    songNotificationControl?.onChange()
                }
                ACTION_PREVIOUS -> {
                    change(-1)
                    songNotificationControl?.onChange()
                }
            }
        }
    }

    fun create(position: Int) {
        index = position
        release()
        mediaPlayer = MediaPlayer.create(
            this,
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songs[index].id)
        )
        start()
        mediaPlayer?.setOnCompletionListener(this)
    }

    fun start() {
        mediaPlayer?.start()
        createNotification()
    }

    fun pause() {
        mediaPlayer?.pause()
        createNotification()
    }

    fun release() {
        mediaPlayer?.release()
    }

    fun getDuration(): Int? = mediaPlayer?.duration

    fun getCurrentPosition(): Int? = mediaPlayer?.currentPosition

    fun getTitle(): String {
        if (songs.size > index) {
            return songs[index].title
        }
        return ""
    }

    fun getArtist(): String = songs[index].artist

    fun isPlaying(): Boolean {
        if (mediaPlayer != null) {
            return mediaPlayer?.isPlaying == true
        }
        return false
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun change(value: Int) {
        index += value
        if (index < 0) {
            index = songs.size - 1
        } else if (index >= songs.size) {
            index = 0
        }
        create(index)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        change(1)
        songNotificationControl?.onChange()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        Log.e("onDestroy", "onDestroy")
        unregisterReceiver(receiver)
    }

    companion object {
        const val CHANNEL_ID = "MusicChannel"
        const val ACTION_NEXT = "action.NEXT"
        const val ACTION_PREVIOUS = "action.PREVIOUS"
        const val ACTION_PLAY = "action.PLAY"
        const val ACTION_CLOSE = "action.CLOSE"
    }
}
