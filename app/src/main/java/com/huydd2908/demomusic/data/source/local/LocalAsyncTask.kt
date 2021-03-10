package com.huydd2908.demomusic.data.source.local

import android.os.AsyncTask
import java.lang.Exception

@Suppress("DEPRECATION")
class LocalAsyncTask<T>(
    private val listener: OnDataLoadListener<T>,
    private val handle: () -> T
) : AsyncTask<Unit, Unit, T>() {

    override fun doInBackground(vararg params: Unit?): T? =
        try {
            handle()
        } catch (e: Exception) {
            null
        }

    override fun onPostExecute(result: T?) {
        super.onPostExecute(result)
        result?.let {
            listener.onSuccess(it)
        } ?: listener.onFail("Get song from device failed")
    }
}
