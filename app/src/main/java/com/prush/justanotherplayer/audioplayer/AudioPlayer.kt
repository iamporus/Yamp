package com.prush.justanotherplayer.audioplayer

import android.content.Context
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.NotificationManager

interface AudioPlayer {

    fun init(context: Context, listener: NotificationManager.OnNotificationPostedListener)

    fun playTracks(
        context: Context,
        tracksList: MutableList<Track>,
        selectedTrackPosition: Int,
        shuffle: Boolean = false
    )

    fun shufflePlayTracks(
        context: Context,
        tracksList: MutableList<Track>
    )

    fun setShuffleMode(context: Context, shuffle: Boolean)

    fun updateTracks(
        context: Context,
        tracksList: MutableList<Track>
    )

    fun cleanup()
}