package com.prush.justanotherplayer.ui.trackslibrary

import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.model.Track_State

interface TrackItemRow: ItemRowView {

    fun setDuration(duration: Long)

    fun setTrackNumber(trackNumber: Int)

    fun setTrackState(state: Track_State)
}