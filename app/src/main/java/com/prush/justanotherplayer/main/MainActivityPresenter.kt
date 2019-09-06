package com.prush.justanotherplayer.main

import android.content.Intent
import android.util.Log
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.ITrackRepository
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.services.SELECTED_TRACK_POSITION
import com.prush.justanotherplayer.services.TRACKS_LIST
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = MainActivityPresenter::class.java.name

class MainActivityPresenter(
    private val mainActivityView: IMainActivityView,
    private val trackRepository: ITrackRepository
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    fun loadLibraryTracks() {

        mainActivityView.showProgress()

        launch {
            try {

                // Even though its ContentLoadingProgressBar,
                // it does not display when only handful of items are to be fetched
                delay(700)

                val trackList = trackRepository.getAllTracks(mainActivityView.getContext())
                withContext(Dispatchers.Main) {
                    if (trackList.isEmpty())
                        mainActivityView.displayEmptyLibrary()
                    else
                        mainActivityView.displayLibraryTracks(trackList)

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

    fun startTrackPlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int) {

        Log.d(TAG, "Track selected for playback $tracksList")

        val intent = Intent(mainActivityView.getViewActivity(), AudioPlayerService::class.java)
        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
        intent.putExtra(TRACKS_LIST, ArrayList(tracksList))
        Util.startForegroundService(mainActivityView.getViewActivity(), intent)
    }

    fun fetchTrackMetadata(trackId: Long) {

        Log.d(TAG, "fetchTrackMetadata $trackId")

        // fetch metadata of currently playing track
        launch {
            try {
                val track = trackRepository.getTrackById(mainActivityView.getContext(), trackId)
                withContext(Dispatchers.Main) {
                    mainActivityView.showNowPlayingTrackMetadata(track)
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")
                mainActivityView.displayError()
            }
        }
    }

    fun setNowPlayingTrack(trackId: Long) {

        Log.d(TAG, "setNowPlayingTrack $trackId")

        launch {
            try {
                val trackList = trackRepository.getAllTracks(mainActivityView.getContext())

                withContext(Dispatchers.Main) {
                    if (trackList.isNotEmpty()) {
                        val index = trackList.indexOfFirst { it.id == trackId }
                        if (index != -1) {
                            val track = trackList[index]

                            track.isCurrentlyPlaying = true

                            trackList[index] = track
                            mainActivityView.displayLibraryTracks(trackList)
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

    fun onCleanup() {
        job.cancel()
    }
}
