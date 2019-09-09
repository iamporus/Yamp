package com.prush.justanotherplayer.ui.albumdetails

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Track

interface AlbumDetailsContract {

    interface View : BaseView {

        fun displayAlbumDetails(album: Album)

        fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>)

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun fetchAlbumDetails(albumId: Long): Album

        fun prepareTrackPlayback(selectedTrackPosition: Int)

        fun onCleanup()
    }
}