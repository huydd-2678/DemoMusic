package com.huydd2908.demomusic.service

interface SongControl {
    fun create(position: Int)
    fun start()
    fun pause()
    fun stop()
    fun release()
    fun getDuration(): Int?
    fun getCurrentPosition(): Int?
    fun getTitle(): String
    fun isPlaying(): Boolean
    fun seekTo(position: Int)
    fun change(value: Int)
}
