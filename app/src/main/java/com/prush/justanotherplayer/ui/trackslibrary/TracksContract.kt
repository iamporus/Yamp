package com.prush.justanotherplayer.ui.trackslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Track

interface TracksContract {

    interface View : BaseView{

        fun displayLibraryTracks(trackList: MutableList<Track>)

        fun displayEmptyLibrary()

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadLibraryTracks()

        fun startTrackPlayback(selectedTrackPosition: Int)

        fun setNowPlayingTrack(trackId: Long)

        fun onCleanup()

    }
}