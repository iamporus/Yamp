package com.prush.justanotherplayer.ui.genredetails

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Genre
import com.prush.justanotherplayer.model.Track

interface GenreDetailsContract {

    interface View : BaseView {

        fun displayGenreDetails(genre: Genre)

        fun displayAlbumDetails(album: Album)

        fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>)

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun fetchGenreDetails(genreId: Long): Genre

        fun loadAlbumDetails(album: Album)

        fun prepareTrackPlayback(selectedTrackPosition: Int)

        fun prepareTrackContextMenu(position: Int)

        fun onCleanup()
    }
}