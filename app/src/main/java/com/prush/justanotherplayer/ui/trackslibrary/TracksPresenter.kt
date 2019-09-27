package com.prush.justanotherplayer.ui.trackslibrary

import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.ITrackRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = TracksPresenter::class.java.name

class TracksPresenter(
    private val trackRepository: ITrackRepository,
    private val view: TracksContract.View
) : TracksContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    private var tracksList = mutableListOf<Track>()

    override fun loadLibraryTracks() {

        view.showProgress()

        launch {
            try {

                tracksList.clear()
                tracksList.addAll(trackRepository.getAllTracks(view.getApplicationContext()))
                withContext(Dispatchers.Main) {
                    if (tracksList.isEmpty())
                        view.displayEmptyLibrary()
                    else {
                        view.displayLibraryTracks(tracksList)
                    }

                    view.hideProgress()
                }

            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")

                withContext(Dispatchers.Main) {
                    view.hideProgress()
                    view.displayError()
                }
            }

        }

    }

    override fun prepareTrackPlayback(selectedTrackPosition: Int) {

        Log.d(TAG, "Track selected for playback $selectedTrackPosition")
        view.startTrackPlayback(selectedTrackPosition, tracksList)
    }

    override fun prepareTrackContextMenu(position: Int) {
        view.showContextMenuForTrack(tracksList[position])
    }

    override fun shuffleTrackPlayback() {
        Log.d(TAG, "Shuffling all Tracks")
        view.startShufflePlayback(tracksList)
    }

    override fun setNowPlayingTrack(trackId: Long) {
        //TODO:
    }

    override fun onCleanup() {
        job.cancel()
        tracksList.clear()
    }
}