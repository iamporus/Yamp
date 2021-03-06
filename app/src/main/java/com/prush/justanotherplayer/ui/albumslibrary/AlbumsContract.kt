package com.prush.justanotherplayer.ui.albumslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Track

interface AlbumsContract {

    interface View : BaseView{

        fun displayAllAlbums(albumsList: MutableList<Album>)

        fun displayEmptyLibrary()

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context

        fun showAlbumDetails(album: Album)
    }

    interface Presenter {

        fun loadLibraryAlbums()

        fun loadAlbumDetails(position: Int)

        fun startAlbumPlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int)

        fun onCleanup()
    }
}