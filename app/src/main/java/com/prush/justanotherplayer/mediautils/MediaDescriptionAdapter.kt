package com.prush.justanotherplayer.mediautils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.queue.NowPlayingQueue
import com.prush.justanotherplayer.ui.main.MainActivity
import com.prush.justanotherplayer.utils.OnBitmapLoadedListener
import com.prush.justanotherplayer.utils.loadAlbumArt

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

            loadAlbumArt(context, getNowPlayingTrack().albumId, object : OnBitmapLoadedListener {
                override fun onBitmapLoaded(resource: Bitmap) {
                    callback?.onBitmap(resource)
                }

                override fun onBitmapLoadingFailed() {
                    callback?.onBitmap(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.ic_audiotrack
                        )?.toBitmap()
                    )
                }

            })
        }
        return BitmapFactory.decodeResource(context.resources, R.drawable.exo_controls_play)
    }

    private fun getNowPlayingTrack() =
        nowPlayingQueue.nowPlayingTracksList[nowPlayingQueue.currentPlayingTrackIndex]
}