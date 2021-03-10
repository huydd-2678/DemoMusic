package com.huydd2908.demomusic.data.source.local

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Build
import android.provider.MediaStore
import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.data.source.SongDataSource

class SongLocalDataSource private constructor(private val contentResolver: ContentResolver) :
    SongDataSource {

    @Suppress("DEPRECATION")
    override fun getSongs(listener: OnDataLoadListener<MutableList<Song>>) {
        LocalAsyncTask(listener) {
            getSongsFromLocal()
        }.execute()
    }

    @SuppressLint("Recycle")
    private fun getSongsFromLocal(): MutableList<Song> {
        val songs = mutableListOf<Song>()
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                songs.add(Song(it))
            }
        }
        return songs
    }

    companion object {
        private var instance: SongLocalDataSource? = null
        fun getInstance(contentResolver: ContentResolver) =
            instance ?: SongLocalDataSource(contentResolver).also { instance = it }
    }
}
