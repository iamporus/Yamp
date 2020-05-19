package com.prush.justanotherplayer.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.prush.justanotherplayer.audioplayer.AudioPlayer
import com.prush.justanotherplayer.audioplayer.ExoPlayer
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.mediautils.NotificationManager
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.queue.NowPlayingInfo
import com.prush.justanotherplayer.queue.NowPlayingQueue
import com.prush.justanotherplayer.queue.QueueManager
import com.prush.justanotherplayer.repositories.*
import com.prush.justanotherplayer.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

private val TAG: String = AudioPlayerService::class.java.name

class AudioPlayerService : Service(), NotificationManager.OnNotificationPostedListener,
    Player.EventListener {

    private lateinit var queueManager: QueueManager
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var tracksRepository: ITrackRepository
    private lateinit var albumRepository: IAlbumRepository
    private lateinit var artistRepository: IArtistRepository
    private lateinit var genreRepository: IGenreRepository
    private lateinit var searchRepository: ISearchRepository

    override fun onBind(intent: Intent?): IBinder? {
        return AudioServiceBinder()
    }

    inner class AudioServiceBinder : Binder() {

        fun getPlayerInstance(): SimpleExoPlayer {
            return (audioPlayer as ExoPlayer).simpleExoPlayer
        }

        fun getNowPlayingQueue(): NowPlayingQueue {
            return (audioPlayer as ExoPlayer).nowPlayingQueue
        }
    }

    override fun onCreate() {
        super.onCreate()

        audioPlayer = Injection.provideAudioPlayer()
        tracksRepository = Injection.provideTrackRepository()
        albumRepository = Injection.provideAlbumRepository()
        artistRepository = Injection.provideArtistRepository()
        genreRepository = Injection.provideGenreRepository()
        searchRepository = Injection.provideSearchRepository()

        audioPlayer.apply {
            init(this@AudioPlayerService)
            setNotificationPostedListener(this@AudioPlayerService)
            setPlayerEventListener(this@AudioPlayerService)
        }

    }

    @Suppress("UNCHECKED_CAST")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "Received ${intent?.action}")

        if (intent == null || intent.action == null)
            return START_STICKY

        when (intent.action) {
            PlaybackControls.PLAY.name -> {

                val selectedTrackPosition = intent.getIntExtra(SELECTED_TRACK_POSITION, -1)

                val playContext: PLAY_CONTEXT =
                    intent.getSerializableExtra(PLAY_CONTEXT_TYPE) as PLAY_CONTEXT

                when (playContext) {

                    PLAY_CONTEXT.LIBRARY_TRACKS -> {

                        val shuffle = intent.getBooleanExtra(SHUFFLE_TRACKS, false)

                        CoroutineScope(IO).launch {

                            val tracksList = tracksRepository.getAllTracks(applicationContext)

                            if (shuffle) {
                                audioPlayer.shufflePlayTracks(applicationContext, tracksList)
                            } else {
                                audioPlayer.playTracks(
                                    applicationContext,
                                    tracksList,
                                    selectedTrackPosition
                                )
                            }

                        }
                    }
                    PLAY_CONTEXT.ALBUM_TRACKS -> {

                        val albumId = intent.getLongExtra(SELECTED_ALBUM_ID, 0L)

                        CoroutineScope(IO).launch {

                            val album = albumRepository.getAlbumById(applicationContext, albumId)

                            audioPlayer.playTracks(
                                applicationContext,
                                album.tracksList,
                                selectedTrackPosition
                            )
                        }
                    }
                    PLAY_CONTEXT.ARTIST_TRACKS -> {

                        val artistId = intent.getLongExtra(SELECTED_ARTIST_ID, 0L)

                        CoroutineScope(IO).launch {

                            val artist =
                                artistRepository.getArtistById(applicationContext, artistId)

                            audioPlayer.playTracks(
                                applicationContext,
                                artist.tracksList,
                                selectedTrackPosition
                            )
                        }
                    }
                    PLAY_CONTEXT.GENRE_TRACKS -> {

                        val genreId = intent.getLongExtra(SELECTED_GENRE_ID, 0L)

                        CoroutineScope(IO).launch {

                            val genre = genreRepository.getGenreById(applicationContext, genreId)

                            audioPlayer.playTracks(
                                applicationContext,
                                genre.tracksList,
                                selectedTrackPosition
                            )
                        }
                    }
                    PLAY_CONTEXT.SEARCH_TRACKS -> {

                        val searchQuery = intent.getStringExtra(SEARCH_QUERY)

                        searchQuery?.let {
                            CoroutineScope(IO).launch {

                                val searchResult =
                                    searchRepository.searchAll(applicationContext, searchQuery)

                                audioPlayer.playTracks(
                                    applicationContext,
                                    searchResult.tracks,
                                    selectedTrackPosition
                                )
                            }
                        }
                    }
                    PLAY_CONTEXT.QUEUE_TRACKS -> {

                        CoroutineScope(IO).launch {

                            queueManager = (audioPlayer as ExoPlayer).nowPlayingQueue
                            val nowPlayingTracks = queueManager.getNowPlayingTracks()

                            audioPlayer.playTracks(
                                applicationContext,
                                nowPlayingTracks,
                                selectedTrackPosition
                            )
                        }

                    }
                }


            }

            PlaybackControls.SHUFFLE_OFF.name -> {

                audioPlayer.setShuffleMode(this, false)
            }

            PlaybackControls.SHUFFLE_ON.name -> {

                audioPlayer.setShuffleMode(this, true)
            }

            PlaybackControls.RE_ORDER.name -> {

                val tracksList: ArrayList<Track> = ArrayList()

                @Suppress("UNCHECKED_CAST")
                tracksList.addAll(intent.getSerializableExtra(TRACKS_LIST) as ArrayList<Track>)

                audioPlayer.updateTracks(this, tracksList)
            }
            PlaybackControls.ADD_TO_QUEUE.name,
            PlaybackControls.PLAY_NEXT.name -> {

                val track = intent.getSerializableExtra(SELECTED_TRACK) as Track
                val playbackStarted =
                    (audioPlayer as ExoPlayer).nowPlayingQueue.nowPlayingTracksList.isNotEmpty()

                if (playbackStarted) {

                    if (intent.action == PlaybackControls.ADD_TO_QUEUE.name)
                        audioPlayer.addTrackToQueue(this, track)
                    else
                        audioPlayer.playNext(this, track)

                } else {

                    //playback is not initiated

                    val tracksList: ArrayList<Track> = ArrayList()

                    @Suppress("UNCHECKED_CAST")
                    tracksList.add(track)
                    audioPlayer.playTracks(this, tracksList, 0)
                }
            }
        }

        return START_STICKY
    }

    override fun onNotificationPosted(notificationId: Int, notification: Notification?) {

        startForeground(notificationId, notification)
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        // removed call to stopSelf() here. This was killing the service when app is restarted
        // again to play some other track and pressing back


    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {

            Player.STATE_IDLE, Player.STATE_ENDED -> {

                stopForeground(true)
            }
            else -> {
                //TODO: figure out what to do here
            }
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {

        updateNowPlaying()
    }

    private fun updateNowPlaying() {
        if ((audioPlayer as ExoPlayer).simpleExoPlayer.currentTag != null) {
            val nowPlayingInfo: NowPlayingInfo =
                (audioPlayer as ExoPlayer).simpleExoPlayer.currentTag as NowPlayingInfo

            (audioPlayer as ExoPlayer).nowPlayingQueue.apply {
                currentPlayingTrackId = nowPlayingInfo.id
                nowPlayingTracksList.forEachIndexed { index, track ->
                    if (track.id == currentPlayingTrackId) {
                        currentPlayingTrackIndex = index
                        return@forEachIndexed
                    }
                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()

        audioPlayer.cleanup()
    }

    enum class PlaybackControls {
        PLAY,
        SHUFFLE_ON,
        SHUFFLE_OFF,
        RE_ORDER,
        ADD_TO_QUEUE,
        PLAY_NEXT
    }

}