package com.prush.justanotherplayer.mediautils

import android.content.Context
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.queue.NowPlayingQueue

private const val TAG = "Yamp!"

class MediaSessionManager(context: Context, private val player: SimpleExoPlayer) {

    private var mediaSessionConnector: MediaSessionConnector
    var mediaSession: MediaSessionCompat = MediaSessionCompat(
        context,
        TAG
    )

    init {
        mediaSession.isActive = false
        mediaSessionConnector = MediaSessionConnector(mediaSession)
    }

    fun setupMediaSessionConnector(context: Context, nowPlayingQueue: NowPlayingQueue) {

        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {

            override fun getMediaDescription(
                player: Player, windowIndex: Int
            ): MediaDescriptionCompat {

                //fetch actual metadata on background thread
                return getMediaDescriptionForLockScreen(
                    context,
                    getTrack(nowPlayingQueue, windowIndex)
                ) {
                    if (windowIndex == nowPlayingQueue.nowPlayingTracksList.size - 1) {
                        invalidateSession()
                    }
                }
            }

        })

        mediaSessionConnector.setPlayer(player)
    }

    private fun getTrack(nowPlayingQueue: NowPlayingQueue, windowIndex: Int): Track {
        return nowPlayingQueue.nowPlayingTracksList[windowIndex]
    }

    fun invalidateSession() {
        //This is to update the lock screen metadata when actual bitmap is fetched from the worker thread
        mediaSessionConnector.invalidateMediaSessionQueue()
        mediaSessionConnector.invalidateMediaSessionMetadata()
    }

    fun cleanup() {
        mediaSession.release()
        mediaSessionConnector.setPlayer(null)
    }
}

