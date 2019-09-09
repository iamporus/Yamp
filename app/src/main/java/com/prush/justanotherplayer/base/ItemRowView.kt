package com.prush.justanotherplayer.base

import android.graphics.Bitmap
import com.prush.justanotherplayer.base.RecyclerAdapter

interface ItemRowView {

    fun setTitle(title: String)

    fun setSubtitle(subtitle: String)

    fun setAlbumArt(resource: Bitmap)

    fun setDuration(duration: Long)

    fun setTrackNumber(trackNumber: Int)

    fun markAsNowPlaying(isNowPlaying: Boolean)

    fun setOnClickListener(position: Int, listener: RecyclerAdapter.OnItemClickListener)
}