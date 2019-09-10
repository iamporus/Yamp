package com.prush.justanotherplayer.base

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.R

class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView)

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    ItemRowView {

    override fun setTitle(title: String) {
        val titleTextView: TextView = itemView.findViewById(R.id.rowTitleTextView)
        titleTextView.text = title
    }

    override fun setSubtitle(subtitle: String) {
        val subtitleTextView: TextView = itemView.findViewById(R.id.rowSubtitleTextView)
        subtitleTextView.text = subtitle
    }

    override fun setAlbumArt(resource: Bitmap) {
        val albumArtImageView: ImageView = itemView.findViewById(R.id.rowArtImageView)
        albumArtImageView.setImageBitmap(resource)
    }

    override fun setOnClickListener(position: Int, listener: RecyclerAdapter.OnItemClickListener) {
        val rowLayout: ViewGroup = itemView.findViewById(R.id.rowLayout)
        rowLayout.setOnClickListener {
            listener.onItemClick(position)
        }
    }
}