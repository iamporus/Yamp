package com.prush.justanotherplayer.base

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.prush.justanotherplayer.R

class HeaderViewHolder(itemView: View) : BaseViewHolder(itemView) {

    override fun setOnClickListener(
        position: Int,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {
        val button: MaterialButton = itemView.findViewById(R.id.headerActionButton)
        button.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    fun setActionText(text: String) {
        val button: MaterialButton = itemView.findViewById(R.id.headerActionButton)
        button.text = text
    }
}

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

    override fun setOnClickListener(
        position: Int,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {
        val rowLayout: ViewGroup = itemView.findViewById(R.id.rowLayout)
        rowLayout.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(listener: RecyclerAdapter.OnItemInteractedListener?) {
        val handleImageView: ImageView? = itemView.findViewById(R.id.dragHandleImageView)

        if (listener != null) {
            handleImageView?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {

                    listener.onDragStarted(this@BaseViewHolder)
                }

                false
            }
        } else {
            handleImageView?.setOnTouchListener(null)
        }
    }
}