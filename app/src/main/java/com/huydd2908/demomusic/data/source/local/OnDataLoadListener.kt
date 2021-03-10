package com.huydd2908.demomusic.data.source.local

interface OnDataLoadListener<T> {
    fun onSuccess(data: T)
    fun onFail(message: String)
}
