package com.huydd2908.demomusic.utils

interface BasePresenter<T> {
    fun onStart()
    fun setView(view: T?)
}
