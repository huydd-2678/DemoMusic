package com.huydd2908.demomusic.data.model

import android.database.Cursor
import android.provider.MediaStore

class Song(var id: Long, var title: String, var artist: String) {
    constructor(cursor: Cursor) : this(
        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))
    )
}
