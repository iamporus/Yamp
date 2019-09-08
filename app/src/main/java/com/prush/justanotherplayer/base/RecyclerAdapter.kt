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
import com.prush.justanotherplayer.ui.trackslibrary.TracksRowView
import com.prush.justanotherplayer.utils.getTimeStringFromSeconds

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

        override fun setTrackDuration(duration: Long) {
            val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
            durationTextView.text = getTimeStringFromSeconds(duration / 1000)
        }

        override fun setTrackTitle(title: String) {
            val titleTextView: TextView = itemView.findViewById(R.id.rowTitleTextView)
            titleTextView.text = title
        }

        override fun setTrackAlbum(album: String) {
            val subtitleTextView: TextView = itemView.findViewById(R.id.rowSubtitleTextView)
            subtitleTextView.text = album
        }

        override fun setTrackAlbumArt(resource: Bitmap) {
            val albumArtImageView: ImageView = itemView.findViewById(R.id.rowArtImageView)
            albumArtImageView.setImageBitmap(resource)
        }

        override fun markTrackAsPlaying(isNowPlaying: Boolean) {
            val nowPlayingImageView: ImageView = itemView.findViewById(R.id.nowPlayingImageView)
            nowPlayingImageView.visibility = if (isNowPlaying) View.VISIBLE else View.GONE
        }

        override fun setOnClickListener(position: Int, listener: OnItemClickListener) {
            val rowLayout: ConstraintLayout = itemView.findViewById(R.id.rowLayout)
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