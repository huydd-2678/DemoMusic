package com.huydd2908.demomusic.ui.song.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huydd2908.demomusic.R
import com.huydd2908.demomusic.data.model.Song
import com.huydd2908.demomusic.utils.OnRecyclerViewItemClickListener
import kotlinx.android.synthetic.main.item_song.view.*

class SongAdapter : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    private val songs = mutableListOf<Song>()
    private var listener: OnRecyclerViewItemClickListener<Song>? = null

    inner class SongViewHolder(
        itemView: View,
        private val listener: OnRecyclerViewItemClickListener<Song>?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindViewData(song: Song) {
            itemView.textTitle.text = song.title
            itemView.textArtist.text = song.artist
            itemView.setOnClickListener {
                listener?.onItemClickListener(song)
            }
        }
    }

    fun updateData(songs: MutableList<Song>) {
        songs.let {
            this.songs.clear()
            this.songs.addAll(songs)
            notifyDataSetChanged()
        }
    }

    fun setListener(listener: OnRecyclerViewItemClickListener<Song>) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bindViewData(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}
