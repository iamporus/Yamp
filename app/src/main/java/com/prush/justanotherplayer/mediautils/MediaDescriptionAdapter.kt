package com.prush.justanotherplayer.mediautils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.queue.NowPlayingQueue
import com.prush.justanotherplayer.ui.main.MainActivity
import com.prush.justanotherplayer.utils.getAlbumArtUri

class MediaDescriptionAdapter(
    private val context: Context,
    private val nowPlayingQueue: NowPlayingQueue
) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    override fun createCurrentContentIntent(player: Player?): PendingIntent? {

        return PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getCurrentSubText(player: Player?): String? {
        if (player != null) {
            return getNowPlayingTrack().artistName
        }
        return ""
    }

    override fun getCurrentContentText(player: Player?): String? {
        if (player != null) {
            return getNowPlayingTrack().albumName
        }
        return ""
    }

    override fun getCurrentContentTitle(player: Player?): String {
        if (player != null) {
            return getNowPlayingTrack().title
        }
        return ""
    }

    override fun getCurrentLargeIcon(
        player: Player?,
        callback: PlayerNotificationManager.BitmapCallback?
    ): Bitmap? {

        if (player != null) {

            Glide.with(context)
                .asBitmap()
                .load(
                    getAlbumArtUri(context, getNowPlayingTrack().albumId)
                )
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback?.onBitmap(resource)
                    }

                })
        }
        return BitmapFactory.decodeResource(context.resources, R.drawable.exo_controls_play)
    }

    private fun getNowPlayingTrack() =
        nowPlayingQueue.nowPlayingTracksList[nowPlayingQueue.currentPlayingTrackIndex]
}