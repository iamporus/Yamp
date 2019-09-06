package com.prush.justanotherplayer.main

import android.util.Log
import com.prush.justanotherplayer.repositories.ITrackRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = MainActivityPresenter::class.java.name

class MainActivityPresenter(
    private val mainActivityView: IMainActivityView,
    private val trackRepository: ITrackRepository
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)


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

    fun onCleanup() {
        job.cancel()
    }
}
