package com.prush.justanotherplayer.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.model.Track

interface NowPlayingContract {

    interface View {

        fun displayError(error: String)

        fun getViewActivity(): AppCompatActivity

        fun showNowPlayingTrackMetadata(track: Track)

        fun getContext(): Context

        fun updateShuffleMode(shuffleMode: Boolean)
    }

    interface Presenter {

        fun fetchTrackMetadata(trackId: Long)

        fun onCleanup()

        fun changeShuffleMode(shuffleMode: Boolean)
    }
}