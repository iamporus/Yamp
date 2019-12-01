package com.prush.justanotherplayer.audioplayer

import android.content.Context
import com.google.android.exoplayer2.Player
import com.prush.justanotherplayer.mediautils.NotificationManager
import com.prush.justanotherplayer.model.Track

interface AudioPlayer {

    fun init(context: Context)

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

    fun setNotificationPostedListener(listener: NotificationManager.OnNotificationPostedListener) {
        //default method
    }

    fun setPlayerEventListener(listener: Player.EventListener) {
        //default method
    }

    fun cleanup()
}