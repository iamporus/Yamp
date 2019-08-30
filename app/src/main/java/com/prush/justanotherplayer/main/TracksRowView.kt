package com.prush.justanotherplayer.main

import android.graphics.Bitmap

interface TracksRowView {

    fun setTrackTitle(title: String)

    fun setTrackAlbum(album: String)

    fun setTrackAlbumArt(resource: Bitmap)
}