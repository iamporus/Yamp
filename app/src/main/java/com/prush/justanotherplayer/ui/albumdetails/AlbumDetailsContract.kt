package com.prush.justanotherplayer.ui.albumdetails

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Album

interface AlbumDetailsContract {

    interface View: BaseView {

        fun displayAlbumDetails(album: Album)

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun fetchAlbumDetails(albumId: Long): Album

        fun onCleanup()
    }
}