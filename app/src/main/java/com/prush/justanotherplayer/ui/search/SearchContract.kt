package com.prush.justanotherplayer.ui.search

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Track

interface SearchContract {

    interface View : BaseView {

        fun displayFoundTracks(trackList: MutableList<Track>)

        fun displayEmptyResult()

        fun displayInfoText()

        fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>)

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadTracksStartingWith(query: String)

        fun prepareTrackPlayback(selectedTrackPosition: Int)

        fun onCleanup()

    }
}