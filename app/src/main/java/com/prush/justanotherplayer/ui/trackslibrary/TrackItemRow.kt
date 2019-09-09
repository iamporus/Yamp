package com.prush.justanotherplayer.ui.trackslibrary

import com.prush.justanotherplayer.base.ItemRowView

interface TrackItemRow: ItemRowView {

    fun setDuration(duration: Long)

    fun setTrackNumber(trackNumber: Int)

    fun markAsNowPlaying(isNowPlaying: Boolean)
}