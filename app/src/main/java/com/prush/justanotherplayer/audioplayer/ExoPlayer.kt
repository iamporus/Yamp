package com.prush.justanotherplayer.audioplayer

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.Track_State
import com.prush.justanotherplayer.services.*
import com.prush.justanotherplayer.utils.shuffleTracks

private val TAG = AudioPlayer::class.java.name

class ExoPlayer : AudioPlayer {

    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var nowPlayingQueue: NowPlayingQueue
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    private lateinit var playbackEventListener: PlaybackEventListener
    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var notificationManager: NotificationManager

    override fun init(
        context: Context,
        listener: NotificationManager.OnNotificationPostedListener
    ) {
        val trackSelector = DefaultTrackSelector()

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.getString(R.string.app_name))
        )

        simpleExoPlayer.addAnalyticsListener(EventLogger(DefaultTrackSelector()))

        concatenatingMediaSource = ConcatenatingMediaSource()

        nowPlayingQueue = NowPlayingQueue()

        playbackEventListener = PlaybackEventListener(simpleExoPlayer, nowPlayingQueue)

        simpleExoPlayer.addListener(playbackEventListener)

        mediaSessionManager = MediaSessionManager(context, simpleExoPlayer)
        notificationManager =
            NotificationManager(
                context,
                mediaSessionManager.mediaSession,
                simpleExoPlayer,
                listener
            )
    }

    override fun playTracks(
        context: Context,
        tracksList: MutableList<Track>,
        selectedTrackPosition: Int, shuffle: Boolean
    ) {

        val tracksPlaylist = mutableListOf<Track>()
        tracksPlaylist.addAll(tracksList)

        if (selectedTrackPosition != -1) {
            tracksPlaylist.mapIndexed { index, track ->
                when {
                    index == selectedTrackPosition -> track.state = Track_State.PLAYING
                    index < selectedTrackPosition -> track.state = Track_State.PLAYED
                    else -> track.state = Track_State.IN_QUEUE
                }
            }
            nowPlayingQueue.currentPlayingTrackIndex = selectedTrackPosition
        }

        concatenatingMediaSource.clear()

        tracksPlaylist.forEachIndexed { index, track ->

            if (index >= selectedTrackPosition) {
                val uri: Uri = track.getPlaybackUri()
                val mediaSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .setTag(NowPlayingInfo(track.id, index))
                        .createMediaSource(uri)

                concatenatingMediaSource.addMediaSource(mediaSource)
            }
        }

        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        simpleExoPlayer.prepare(concatenatingMediaSource)
        simpleExoPlayer.playWhenReady = true

        nowPlayingQueue.setupQueue(tracksPlaylist, shuffle)

        notificationManager.setupPlayerNotification(context, nowPlayingQueue)

        mediaSessionManager.setupMediaSessionConnector(context, nowPlayingQueue)
    }

    override fun shufflePlayTracks(context: Context, tracksList: MutableList<Track>) {

        nowPlayingQueue.apply {

            keepUnShuffledTracks(tracksList)
        }

        shuffleTracks(0, tracksList)

        playTracks(context, tracksList, -1, true)
    }

    override fun setShuffleMode(context: Context, shuffle: Boolean) {

        val tracksList: ArrayList<Track> = ArrayList()

        tracksList.addAll(nowPlayingQueue.trackListUnShuffled)

        val nowPlayingInfo = simpleExoPlayer.currentTag as NowPlayingInfo

        var tempIndex = -1
        tracksList.forEachIndexed { index, track ->
            if (nowPlayingInfo.id == track.id) {

                tempIndex = index
                return@forEachIndexed
            }
        }

        tempIndex = if (tempIndex != -1) tempIndex else 0

        tracksList.mapIndexed { index, track ->
            when {
                index == tempIndex -> track.state = Track_State.PLAYING
                index < tempIndex -> track.state = Track_State.PLAYED
                else -> track.state = Track_State.IN_QUEUE
            }
        }


        if (shuffle) {
            shuffleTracks(tempIndex, tracksList)
        }


        val mediaSourceDiffUtilCallback =
            MediaSourceDiffUtilCallback(nowPlayingQueue.nowPlayingTracksList, tracksList)
        val diffResult = DiffUtil.calculateDiff(mediaSourceDiffUtilCallback)

        diffResult.dispatchUpdatesTo(MediaSourceListUpdateCallback(concatenatingMediaSource))

        nowPlayingQueue.setupQueue(tracksList, false)

        notificationManager.setupPlayerNotification(context, nowPlayingQueue)

        mediaSessionManager.setupMediaSessionConnector(context, nowPlayingQueue)
    }

    override fun updateTracks(
        context: Context,
        tracksList: MutableList<Track>
    ) {

        val mediaSourceDiffUtilCallback =
            MediaSourceDiffUtilCallback(nowPlayingQueue.nowPlayingTracksList, tracksList)
        val diffResult = DiffUtil.calculateDiff(mediaSourceDiffUtilCallback)

        diffResult.dispatchUpdatesTo(MediaSourceListUpdateCallback(concatenatingMediaSource))

        nowPlayingQueue.setupQueue(tracksList, nowPlayingQueue.shuffleEnabled)

        notificationManager.setupPlayerNotification(context, nowPlayingQueue)

        mediaSessionManager.setupMediaSessionConnector(context, nowPlayingQueue)
    }

    override fun cleanup() {

        mediaSessionManager.cleanup()
        notificationManager.cleanup()

        simpleExoPlayer.removeListener(playbackEventListener)
        simpleExoPlayer.release()
    }
}