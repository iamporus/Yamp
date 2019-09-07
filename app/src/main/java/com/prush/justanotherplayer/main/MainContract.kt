package com.prush.justanotherplayer.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.model.Track

interface MainContract {

    interface View {

        fun displayError()

        fun getViewActivity(): AppCompatActivity

        fun showNowPlayingTrackMetadata(track: Track)

        fun showPermissionRationale(permission: String, requestCode: Int)

        fun getContext(): Context
    }

    interface Presenter {

        fun fetchTrackMetadata(trackId: Long)

        fun onCleanup()
    }
}