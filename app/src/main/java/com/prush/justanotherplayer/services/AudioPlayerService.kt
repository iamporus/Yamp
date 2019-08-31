package com.prush.justanotherplayer.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.model.Track
import java.util.*

private val TAG: String = AudioPlayerService::class.java.name

class AudioPlayerService : Service() {

    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "Received ${intent?.action}")

        when (intent?.action) {
            PlaybackControls.PLAY.name -> {

                val trackPosition = intent.getIntExtra(SELECTED_TRACK_POSITION, -1)

                @Suppress("UNCHECKED_CAST")
                val tracksList: List<Track> =
                    intent.getSerializableExtra(TRACKS_LIST) as List<Track>

                Collections.rotate(tracksList, 0 - trackPosition)

                val concatenatingMediaSource = ConcatenatingMediaSource()

                for (track in tracksList) {

                    val uri: Uri = track.getPlaybackUri()
                    val mediaSource =
                        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

                    concatenatingMediaSource.addMediaSource(mediaSource)
                }

                simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                simpleExoPlayer.prepare(concatenatingMediaSource)
                simpleExoPlayer.playWhenReady = true

                val context: Context = this

                playerNotificationManager =
                    PlayerNotificationManager.createWithNotificationChannel(context,
                        PLAYBACK_CHANNEL_ID,
                        R.string.app_name,
                        R.string.app_name,
                        1,
                        MediaDescriptionAdapter(context, tracksList),
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

                mediaSession = MediaSessionCompat(context, TAG)
                mediaSession.isActive = true
                playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)

                mediaSessionConnector = MediaSessionConnector(mediaSession)
                mediaSessionConnector.setQueueNavigator(object :
                    TimelineQueueNavigator(mediaSession) {
                    override fun getMediaDescription(
                        player: Player?,
                        windowIndex: Int
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

                mediaSessionConnector.setPlayer(simpleExoPlayer)
            }
        }
        return START_STICKY
    }

    fun invalidateSession(){
        //This is to update the lock screen metadata when actual bitmap is fetched from the worker thread
        mediaSessionConnector.invalidateMediaSessionQueue()
        mediaSessionConnector.invalidateMediaSessionMetadata()
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

        mediaSession.release()
        mediaSessionConnector.setPlayer(null)
        playerNotificationManager.setPlayer(null)
        simpleExoPlayer.release()

    }

    enum class PlaybackControls {
        PLAY,
    }

}