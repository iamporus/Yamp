package com.prush.justanotherplayer.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.model.Track

interface IMainActivityView {

    fun displayError()

    fun getViewActivity(): AppCompatActivity

    fun showNowPlayingTrackMetadata(track: Track)

    fun showPermissionRationale(permission: String, requestCode: Int)

    fun showProgress()

    fun hideProgress()

    fun getContext(): Context
}
