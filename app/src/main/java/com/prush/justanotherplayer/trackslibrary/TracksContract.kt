package com.prush.justanotherplayer.trackslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.model.Track

interface TracksContract {

    interface View {

        var tracksPresenter: Presenter

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