package com.prush.justanotherplayer.base

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.prush.justanotherplayer.R

class HeaderViewHolder(private val rowItemView: View) : BaseViewHolder(rowItemView) {

    override fun setOnClickListener(
        position: Int,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {
        val button: MaterialButton = rowItemView.findViewById(R.id.headerActionButton)
        button.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    fun setActionText(text: String) {
        val button: MaterialButton = rowItemView.findViewById(R.id.headerActionButton)
        button.text = text
    }
}

open class BaseViewHolder(private val rowItemView: View) : RecyclerView.ViewHolder(rowItemView),
    ItemRowView {

    override fun setTitle(title: String) {
        val titleTextView: TextView = rowItemView.findViewById(R.id.rowTitleTextView)
        titleTextView.text = title
    }

    override fun setSubtitle(subtitle: String) {
        val subtitleTextView: TextView = rowItemView.findViewById(R.id.rowSubtitleTextView)
        subtitleTextView.text = subtitle
    }

    override fun setAlbumArt(resource: Bitmap) {
        val albumArtImageView: ImageView = rowItemView.findViewById(R.id.rowArtImageView)
        albumArtImageView.setImageBitmap(resource)
    }

    override fun setOnClickListener(
        position: Int,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {
        val rowLayout: ViewGroup = rowItemView.findViewById(R.id.rowLayout)
        rowLayout.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun setOnContextMenuClickListener(
        position: Int,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {
        val moreImageView: ImageView = rowItemView.findViewById(R.id.moreImageView)
        moreImageView.setOnClickListener {
            listener.onContextMenuClicked(position)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(listener: RecyclerAdapter.OnItemInteractedListener?) {
        val handleImageView: ImageView? = rowItemView.findViewById(R.id.dragHandleImageView)

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

    override fun cleanup() {
        val albumArtImageView: ImageView? = rowItemView.findViewById(R.id.rowArtImageView)
        if (albumArtImageView != null)
            Glide.with(rowItemView).clear(albumArtImageView)
    }
}