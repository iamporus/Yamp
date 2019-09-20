package com.prush.justanotherplayer.services

import android.content.Context
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.prush.justanotherplayer.model.Track

private const val TAG = "Yamp!"

class MediaSessionManager(context: Context, private val player: SimpleExoPlayer) {

    private var mediaSessionConnector: MediaSessionConnector
    var mediaSession: MediaSessionCompat = MediaSessionCompat(context, TAG)

    init {
        mediaSession.isActive = false
        mediaSessionConnector = MediaSessionConnector(mediaSession)
    }

    fun setupMediaSessionConnector(context: Context, tracksList: List<Track>) {

        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {

            override fun getMediaDescription(
                player: Player?, windowIndex: Int
            ): MediaDescriptionCompat {

                if (player != null && tracksList[player.currentWindowIndex].albumArtBitmap == null) {

                    //fetch actual metadata on background thread
                    return getMediaDescriptionForLockScreen(
                        context,
                        tracksList[player.currentWindowIndex]
                    ) { invalidateSession() }
                }

                //return default empty metadata to lock screen
                return MediaSessionConnector.DefaultMediaMetadataProvider(
                    mediaSession.controller,
                    TAG
                )
                    .getMetadata(player).description
            }

        })

        mediaSessionConnector.setPlayer(player)
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