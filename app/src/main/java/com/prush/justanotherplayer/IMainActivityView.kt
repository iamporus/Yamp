package com.prush.justanotherplayer

import com.prush.justanotherplayer.model.Track

interface IMainActivityView {

    fun displayLibraryTracks(trackList: MutableList<Track>)

    fun displayEmptyLibrary()
}
