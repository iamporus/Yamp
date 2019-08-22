package com.prush.justanotherplayer

import android.util.Log
import com.prush.justanotherplayer.repositories.ITrackRepository

class MainActivityPresenter(
    val mainActivityView: IMainActivityView,
    val trackRepository: ITrackRepository
) {

    private val TAG = javaClass.name

    fun displayAllTracks() {

        try {
            val trackList = trackRepository.getAllTracks()
            if (trackList.isEmpty())
                mainActivityView.displayEmptyLibrary()
            else
                mainActivityView.displayLibraryTracks(trackList)
        } catch (e: RuntimeException) {
            Log.d(TAG, "Exception: ${e.message}")
            mainActivityView.displayError()
        }
    }
}
