package com.prush.justanotherplayer.base

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.trackslibrary.TracksRowView

class RecyclerAdapter<T>(
    private val listPresenter: ListPresenter<T>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                listPresenter.rowLayoutId,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listPresenter.getItemsCount()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listPresenter.onBindTrackRowViewAtPosition(
            holder.itemView.context,
            holder,
            position,
            itemClickListener
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        TracksRowView {

        private var rowLayout: ConstraintLayout = itemView.findViewById(R.id.rowLayout)
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

        override fun setOnClickListener(position: Int, listener: OnItemClickListener) {
            rowLayout.setOnClickListener {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(
            selectedTrackPosition: Int
        )
    }
}