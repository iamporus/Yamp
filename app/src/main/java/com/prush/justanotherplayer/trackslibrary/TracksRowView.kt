package com.prush.justanotherplayer.trackslibrary

import android.graphics.Bitmap

interface TracksRowView {

    fun setTrackTitle(title: String)

    fun setTrackAlbum(album: String)

    fun setTrackAlbumArt(resource: Bitmap)

    fun markTrackAsPlaying(isNowPlaying: Boolean)
}