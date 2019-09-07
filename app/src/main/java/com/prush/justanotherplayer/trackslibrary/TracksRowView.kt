package com.prush.justanotherplayer.trackslibrary

import android.graphics.Bitmap
import com.prush.justanotherplayer.base.RecyclerAdapter

interface TracksRowView {

    fun setTrackTitle(title: String)

    fun setTrackAlbum(album: String)

    fun setTrackAlbumArt(resource: Bitmap)

    fun markTrackAsPlaying(isNowPlaying: Boolean)

    fun setOnClickListener(position: Int, listener: RecyclerAdapter.OnItemClickListener)
}