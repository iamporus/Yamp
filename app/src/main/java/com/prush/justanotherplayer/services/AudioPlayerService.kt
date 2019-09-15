package com.prush.justanotherplayer.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
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
import com.google.android.exoplayer2.source.ShuffleOrder
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.Track_State
import java.util.*
import kotlin.collections.ArrayList

private val TAG: String = AudioPlayerService::class.java.name

class AudioPlayerService : Service() {

    private lateinit var playbackEventListener: PlaybackEventListener
    private lateinit var nowPlayingQueue: NowPlayingQueue
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return AudioServiceBinder()
    }

    inner class AudioServiceBinder : Binder() {

        fun getPlayerInstance(): SimpleExoPlayer {
            return simpleExoPlayer
        }

        fun getNowPlayingQueue(): NowPlayingQueue {
            return nowPlayingQueue
        }
    }

    override fun onCreate() {
        super.onCreate()

        val context: Context = this

        val trackSelector = DefaultTrackSelector()
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, getString(R.string.app_name))
        )
        simpleExoPlayer.addAnalyticsListener(EventLogger(DefaultTrackSelector()))

        mediaSession = MediaSessionCompat(context, TAG)
        mediaSession.isActive = false

        mediaSessionConnector = MediaSessionConnector(mediaSession)

        playerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(
                context,
                PLAYBACK_CHANNEL_ID,
                R.string.app_name,
                R.string.app_name,
                1,
                null
            )

        nowPlayingQueue = NowPlayingQueue()

        playbackEventListener = PlaybackEventListener(simpleExoPlayer, nowPlayingQueue)

        simpleExoPlayer.addListener(playbackEventListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "Received ${intent?.action}")

        val concatenatingMediaSource = ConcatenatingMediaSource()
        var tracksList: ArrayList<Track> = ArrayList()

        var resetPosition = true
        var resetState = true

        when (intent?.action) {
            PlaybackControls.PLAY.name -> {

                val trackPosition = intent.getIntExtra(SELECTED_TRACK_POSITION, -1)

                val shuffle = intent.getBooleanExtra(SHUFFLE_TRACKS, false)

                @Suppress("UNCHECKED_CAST")
                tracksList.addAll(intent.getSerializableExtra(TRACKS_LIST) as ArrayList<Track>)

                if (trackPosition != -1)
                    Collections.rotate(tracksList, 0 - trackPosition)


                nowPlayingQueue.apply {
                    currentPlayingTrackIndex = 0
                }


                if (shuffle) {

                    nowPlayingQueue.setNowPlayingTracks(tracksList.toMutableList(), true)

                    tracksList =
                        shuffleTracks(tracksList, concatenatingMediaSource) as ArrayList<Track>

                    nowPlayingQueue.apply {
                        nowPlayingQueue.setNowPlayingTracks(tracksList.toMutableList())
                        trackList[currentPlayingTrackIndex].state = Track_State.PLAYING
                        shuffleEnabled = true
                    }

                } else {
                    nowPlayingQueue.apply {
                        setNowPlayingTracks(tracksList.toMutableList())
                        trackList[currentPlayingTrackIndex].state = Track_State.PLAYING
                    }
                }


            }

            PlaybackControls.SHUFFLE_OFF.name -> {

                tracksList.addAll(nowPlayingQueue.trackListUnShuffled)

                val nowPlayingInfo = simpleExoPlayer.currentTag as NowPlayingInfo

                var tempIndex = 0
                tracksList.forEachIndexed { index, track ->
                    if (nowPlayingInfo.id == track.id) {

                        tempIndex = index
                        return@forEachIndexed
                    }
                }

                if (tempIndex != -1)
                    Collections.rotate(tracksList, 0 - tempIndex)


                nowPlayingQueue.apply {
                    setNowPlayingTracks(tracksList.toMutableList())
                    currentPlayingTrackIndex = 0
                    shuffleEnabled = false
                }

                resetPosition = false

            }
            PlaybackControls.SHUFFLE_ON.name -> {

                tracksList.addAll(nowPlayingQueue.trackListUnShuffled)

                tracksList = shuffleTracks(tracksList, concatenatingMediaSource) as ArrayList<Track>

                val nowPlayingInfo = simpleExoPlayer.currentTag as NowPlayingInfo

                var tempIndex = 0

                tracksList.forEachIndexed { index, track ->
                    if (nowPlayingInfo.id == track.id) {

                        tempIndex = index
                        return@forEachIndexed
                    }
                }

                if (tempIndex != -1)
                    Collections.rotate(tracksList, 0 - tempIndex)

                nowPlayingQueue.apply {
                    setNowPlayingTracks(tracksList.toMutableList())
                    currentPlayingTrackIndex = 0
                    shuffleEnabled = true
                }

                resetPosition = false

            }
        }

        Log.d(TAG, "Now Playing Queue - $tracksList")
        tracksList.forEachIndexed { index, track ->

            val uri: Uri = track.getPlaybackUri()
            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .setTag(NowPlayingInfo(track.id, index))
                    .createMediaSource(uri)

            concatenatingMediaSource.addMediaSource(mediaSource)
        }

        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        simpleExoPlayer.prepare(concatenatingMediaSource, resetPosition, resetState)
        simpleExoPlayer.playWhenReady = true

        val context: Context = this

        setupPlayerNotification(context, tracksList)

        setupMediaSessionConnector(context, tracksList)

        return START_STICKY
    }

    private fun shuffleTracks(
        tracksList: List<Track>,
        concatenatingMediaSource: ConcatenatingMediaSource
    ): List<Track> {

        val shuffledTracks = tracksList.shuffled()
        val shuffledOrder = mutableSetOf<Int>()

        for (track in tracksList) {

            shuffledTracks.forEachIndexed { index, newTrack ->
                if (newTrack.id == track.id) {
                    shuffledOrder.add(index)
                }
            }
        }

        concatenatingMediaSource.setShuffleOrder(
            ShuffleOrder.DefaultShuffleOrder(
                shuffledOrder.toIntArray(),
                2
            )
        )

        (tracksList as ArrayList).apply {
            clear()
            addAll(shuffledTracks)
        }

        return shuffledTracks
    }


    private fun setupMediaSessionConnector(context: Context, tracksList: List<Track>) {

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

        mediaSessionConnector.setPlayer(simpleExoPlayer)
    }

    private fun setupPlayerNotification(context: Context, tracksList: List<Track>) {

        playerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(
                context,
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
                        notificationId: Int, dismissedByUser: Boolean
                    ) {
                        stopSelf()
                    }
                })

        playerNotificationManager.setPlayer(simpleExoPlayer)

        mediaSession.isActive = true
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
    }

    fun invalidateSession() {
        //This is to update the lock screen metadata when actual bitmap is fetched from the worker thread
        mediaSessionConnector.invalidateMediaSessionQueue()
        mediaSessionConnector.invalidateMediaSessionMetadata()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSession.release()
        mediaSessionConnector.setPlayer(null)
        playerNotificationManager.setPlayer(null)
        simpleExoPlayer.removeListener(playbackEventListener)
        simpleExoPlayer.release()

    }

    enum class PlaybackControls {
        PLAY,
        SHUFFLE_ON,
        SHUFFLE_OFF,

    }

}