package com.prush.justanotherplayer.albumslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Track

interface AlbumsContract {

    interface View {

        var albumsPresenter: Presenter

        fun displayAllAlbums(albumsList: MutableList<Album>)

        fun displayEmptyLibrary()

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadLibraryAlbums()

        fun loadAlbumDetails(albumId: Long)

        fun startAlbumPlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int)

        fun onCleanup()
    }
}