package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Track

interface QueueContract {

    interface View : BaseView{

        fun displayNowPlayingTracks(trackList: MutableList<Track>)

        fun displayEmptyQueue()

        fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>)

        fun updateNowPlaying()

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadNowPlayingTracks()

        fun prepareTrackPlayback(selectedTrackPosition: Int)

        fun setNowPlayingTrack(trackId: Long)

        fun onCleanup()

    }
}