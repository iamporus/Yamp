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

class MainActivityPresenter(
    private val mainActivityView: IMainActivityView,
    private val trackRepository: ITrackRepository
) : CoroutineScope {
    private val TAG = javaClass.name

    private val job = Job()
    override val coroutineContext: CoroutineContext = (Dispatchers.IO + job)

    fun loadLibraryTracks() {

        launch {
            try {

                val trackList = trackRepository.getAllTracks()
                withContext(Dispatchers.Main) {
                    if (trackList.isEmpty())
                        mainActivityView.displayEmptyLibrary()
                    else
                        mainActivityView.displayLibraryTracks(trackList)
                }

            } catch (e: RuntimeException) {
                e.printStackTrace()
                Log.d(TAG, "Exception: ${e.message}")
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

    fun onCleanup() {
        job.cancel()
    }

    fun fetchTrackMetadata(trackId: Long) {

        Log.d(TAG, "fetchTrackMetadata $trackId")

        launch {
            val track = trackRepository.getTrackById(trackId)
            withContext(Dispatchers.Main) {
                mainActivityView.showNowPlayingTrackMetadata(track)
            }
        }
    }


}
