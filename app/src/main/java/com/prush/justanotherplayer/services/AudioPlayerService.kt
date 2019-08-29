package com.prush.justanotherplayer.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.main.MainActivity
import com.prush.justanotherplayer.model.Track
import java.util.Collections

class AudioPlayerService : Service() {

    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var simpleExoPlayer: SimpleExoPlayer

    @JvmField
    val TAG: String = AudioPlayerService::class.java.name


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "Received ${intent?.action}")

        when (intent?.action) {
            PlaybackControls.PLAY.name -> {

                val trackPosition = intent.getIntExtra(SELECTED_TRACK_POSITION, -1)
                val tracksList: List<Track> = intent.getSerializableExtra(TRACKS_LIST) as List<Track>

                Collections.rotate(tracksList, 0 - trackPosition)

                val concatenatingMediaSource = ConcatenatingMediaSource()

                for (track in tracksList){

                    val uri: Uri? =
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, track.id)
                    val mediaSource =
                        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

                    concatenatingMediaSource.addMediaSource(mediaSource)
                }

                simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                simpleExoPlayer.prepare(concatenatingMediaSource)
                simpleExoPlayer.playWhenReady = true

                val context: Context = this

                playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context,
                    PLAYBACK_CHANNEL_ID,
                    R.string.app_name,
                    R.string.app_name,
                    1,
                    object : MediaDescriptionAdapter {
                        override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                            return PendingIntent.getActivity(
                                context,
                                0,
                                Intent(context, MainActivity::class.java),
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        }

                        override fun getCurrentSubText(player: Player?): String? {
                            return "Playing Track"
                        }

                        override fun getCurrentContentText(player: Player?): String? {
                            return ""
                        }

                        override fun getCurrentContentTitle(player: Player?): String {
                            if (player != null) {
                                return tracksList[player.currentWindowIndex].title
                            }
                            return ""
                        }

                        override fun getCurrentLargeIcon(
                            player: Player?,
                            callback: PlayerNotificationManager.BitmapCallback?
                        ): Bitmap? {
                            return BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.exo_controls_play
                            )
                        }
                    },
                    object : PlayerNotificationManager.NotificationListener {
                        override fun onNotificationPosted(
                            notificationId: Int,
                            notification: Notification?,
                            ongoing: Boolean
                        ) {
                            startForeground(notificationId, notification)
                        }

                        override fun onNotificationCancelled(
                            notificationId: Int,
                            dismissedByUser: Boolean
                        ) {
                            stopSelf()
                        }
                    })

                playerNotificationManager.setPlayer(simpleExoPlayer)
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        val context: Context = this

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())

        dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, getString(R.string.app_name))
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        playerNotificationManager.setPlayer(null)
        simpleExoPlayer.release()

    }

    enum class PlaybackControls {
        PLAY,
    }

}