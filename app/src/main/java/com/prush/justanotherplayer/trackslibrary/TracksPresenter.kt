package com.prush.justanotherplayer.trackslibrary

import android.content.Intent
import android.util.Log
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.main.IMainActivityView
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.ITrackRepository
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.services.SELECTED_TRACK_POSITION
import com.prush.justanotherplayer.services.TRACKS_LIST
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = TracksPresenter::class.java.name

class TracksPresenter(
    private val trackRepository: ITrackRepository,
    private val view: TracksContract.View,
    private val mainActivityView: IMainActivityView
) : TracksContract.Presenter, CoroutineScope {

    init {
        view.tracksPresenter = this
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    private var tracksList = mutableListOf<Track>()

    override fun loadLibraryTracks() {

        mainActivityView.showProgress()

        launch {
            try {

                // Even though its ContentLoadingProgressBar,
                // it does not display when only handful of items are to be fetched
                delay(700)

                val trackList = trackRepository.getAllTracks(view.getApplicationContext())
                withContext(Dispatchers.Main) {
                    if (trackList.isEmpty())
                        view.displayEmptyLibrary()
                    else {
                        view.displayLibraryTracks(trackList)
                        this@TracksPresenter.tracksList = trackList
                    }

                    mainActivityView.hideProgress()
                }

            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")

                mainActivityView.hideProgress()
                mainActivityView.displayError()
            }

        }

    }

    override fun startTrackPlayback(selectedTrackPosition: Int) {

        Log.d(TAG, "Track selected for playback $tracksList")

        val intent = Intent(view.getViewActivity(), AudioPlayerService::class.java)
        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
        intent.putExtra(TRACKS_LIST, ArrayList(tracksList))
        Util.startForegroundService(view.getViewActivity(), intent)
    }

    override fun setNowPlayingTrack(trackId: Long) {
        Log.d(TAG, "setNowPlayingTrack $trackId")

        launch {
            try {
                val trackList = trackRepository.getAllTracks(view.getApplicationContext())

                withContext(Dispatchers.Main) {
                    if (trackList.isNotEmpty()) {
                        val index = trackList.indexOfFirst { it.id == trackId }
                        if (index != -1) {
                            val track = trackList[index]

                            track.isCurrentlyPlaying = true

                            trackList[index] = track
                            view.displayLibraryTracks(trackList)
                        }
                    }
                }

            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")
                mainActivityView.displayError()
            }

        }
    }

    override fun onCleanup() {
        job.cancel()
        tracksList.clear()
    }
}