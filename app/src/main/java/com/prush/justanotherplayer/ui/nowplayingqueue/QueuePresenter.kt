package com.prush.justanotherplayer.ui.nowplayingqueue

import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.QueueManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = QueuePresenter::class.java.name

class QueuePresenter(
    private val queueManager: QueueManager,
    private val view: QueueContract.View
) : QueueContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    private var tracksList = mutableListOf<Track>()


    override fun loadNowPlayingTracks() {

        Log.d(TAG, "loadNowPlayingTracks()")
        view.showProgress()

        launch {
            try {

                tracksList.addAll(queueManager.getNowPlayingTracks())

                withContext(Dispatchers.Main) {
                    if (tracksList.isEmpty())
                        view.displayEmptyQueue()
                    else {
                        view.displayNowPlayingTracks(tracksList)
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


    override fun setNowPlayingTrack(trackId: Long) {
        Log.d(TAG, "setNowPlayingTrack $trackId")

    }

    override fun onCleanup() {
        job.cancel()
        tracksList.clear()
        Log.d(TAG, "onCleanup()")
    }
}