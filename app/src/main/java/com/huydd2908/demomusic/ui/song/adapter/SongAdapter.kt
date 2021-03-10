package com.huydd2908.demomusic.ui.song.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huydd2908.demomusic.R
import com.huydd2908.demomusic.data.model.Song
import kotlinx.android.synthetic.main.item_song.view.*

class SongAdapter(private var itemClick: (Song) -> Unit) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val songs = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bindViewData(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    fun updateData(newSong: MutableList<Song>) {
        songs.run {
            clear()
            addAll(newSong)
            notifyDataSetChanged()
        }
    }

    class SongViewHolder(
        itemView: View,
        private val itemClick: (Song) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private var song: Song? = null

        init {
            itemView.setOnClickListener {
                song?.let(itemClick)
            }
        }

        fun bindViewData(song: Song) {
            this.song = song
            itemView.apply {
                textTitle.text = song.title
                textArtist.text = song.artist
            }
        }
    }
}
