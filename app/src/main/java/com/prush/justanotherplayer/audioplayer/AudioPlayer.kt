package com.prush.justanotherplayer.audioplayer

import android.content.Context
import com.prush.justanotherplayer.mediautils.NotificationManager
import com.prush.justanotherplayer.model.Track

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

    fun addTrackToQueue(context: Context, track: Track)

    fun playNext(context: Context, track: Track)

    fun cleanup()
}