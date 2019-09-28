package com.prush.justanotherplayer.ui.trackslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Track

interface TracksContract {

    interface View : BaseView{

        fun displayLibraryTracks(trackList: MutableList<Track>)

        fun displayEmptyLibrary()

        fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>)

        fun startShufflePlayback(tracksList: MutableList<Track>)

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadLibraryTracks()

        fun prepareTrackPlayback(selectedTrackPosition: Int)

        fun prepareTrackContextMenu(position: Int)

        fun shuffleTrackPlayback()

        fun setNowPlayingTrack(trackId: Long)

        fun onCleanup()

    }
}