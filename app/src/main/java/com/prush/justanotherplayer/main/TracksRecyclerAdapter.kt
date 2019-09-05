package com.prush.justanotherplayer.main

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.model.Track

class TracksRecyclerAdapter(
    private val tracksListPresenter: TracksListPresenter,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<TracksRecyclerAdapter.TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.track_list_item_row,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return tracksListPresenter.getTrackListCount()
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        tracksListPresenter.onBindTrackRowViewAtPosition(holder, position, itemClickListener)
    }

    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        TracksRowView {

        private var titleTextView: TextView = itemView.findViewById(R.id.bottomTitleTextView)
        private var subtitleTextView: TextView = itemView.findViewById(R.id.bottomSubtitleTextView)
        private var albumArtImageView: ImageView =
            itemView.findViewById(R.id.bottomAlbumArtImageView)
        private var nowPlayingImageView: ImageView = itemView.findViewById(R.id.nowPlayingImageView)

        override fun setTrackTitle(title: String) {
            titleTextView.text = title
        }

        override fun setTrackAlbum(album: String) {
            subtitleTextView.text = album
        }

        override fun setTrackAlbumArt(resource: Bitmap) {
            albumArtImageView.setImageBitmap(resource)
        }

        override fun markTrackAsPlaying(isNowPlaying: Boolean) {
            nowPlayingImageView.visibility = if (isNowPlaying) View.VISIBLE else View.GONE
        }

    }

    interface OnItemClickListener {
        fun onItemClick(
            tracksList: MutableList<Track>,
            selectedTrackPosition: Int
        )
    }
}