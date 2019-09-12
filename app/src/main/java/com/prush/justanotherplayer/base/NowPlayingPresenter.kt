package com.prush.justanotherplayer.base

import android.util.Log
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.repositories.ITrackRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val TAG = NowPlayingPresenter::class.java.name

class NowPlayingPresenter(
    private val view: NowPlayingContract.View,
    private val trackRepository: ITrackRepository
) : NowPlayingContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)


    override fun fetchTrackMetadata(trackId: Long) {

        Log.d(TAG, "fetchTrackMetadata $trackId")

        // fetch metadata of currently playing track
        launch {
            try {
                val track = trackRepository.getTrackById(view.getContext(), trackId)
                withContext(Dispatchers.Main) {
                    view.showNowPlayingTrackMetadata(track)
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")
                view.displayError(view.getContext().getString(R.string.error_sdcard))
            }
        }
    }

    override fun onCleanup() {
        job.cancel()
    }
}
