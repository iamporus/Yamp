package com.prush.justanotherplayer.mediautils

import android.app.Notification
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat

import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.queue.NowPlayingQueue
import com.prush.justanotherplayer.utils.PLAYBACK_CHANNEL_ID

class NotificationManager(
    context: Context,
    private val mediaSession: MediaSessionCompat,
    private val player: SimpleExoPlayer,
    private val listener: OnNotificationPostedListener
) {

    private var playerNotificationManager: PlayerNotificationManager

    init {
        playerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(
                context,
                PLAYBACK_CHANNEL_ID,
                R.string.app_name,
                R.string.app_name,
                1,
                null
            )

    }

    interface OnNotificationPostedListener {
        fun onNotificationPosted(notificationId: Int, notification: Notification?)

        fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean)
    }

    fun setupPlayerNotification(context: Context, nowPlayingQueue: NowPlayingQueue) {

        playerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(
                context,
                PLAYBACK_CHANNEL_ID,
                R.string.app_name,
                R.string.app_name,
                1,
                MediaDescriptionAdapter(
                    context,
                    nowPlayingQueue
                ),

                object : PlayerNotificationManager.NotificationListener {
                    override fun onNotificationPosted(
                        notificationId: Int,
                        notification: Notification?,
                        ongoing: Boolean
                    ) {
                        listener.onNotificationPosted(notificationId, notification)
                    }

                    override fun onNotificationCancelled(
                        notificationId: Int, dismissedByUser: Boolean
                    ) {
                        listener.onNotificationCancelled(notificationId, dismissedByUser)
                    }
                })

        playerNotificationManager.setPlayer(player)

        mediaSession.isActive = true
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
    }

    fun cleanup() {
        playerNotificationManager.setPlayer(null)
    }
}