package com.prush.justanotherplayer

import com.prush.justanotherplayer.repositories.ITrackRepository

class MainActivityPresenter(
    val mainActivityView: IMainActivityView,
    val trackRepository: ITrackRepository
) {

    fun displayAllTracks() {

        val trackList = trackRepository.getAllTracks()

        if (trackList.isEmpty())
            mainActivityView.displayEmptyLibrary()
        else
            mainActivityView.displayLibraryTracks(trackList)
    }
}
