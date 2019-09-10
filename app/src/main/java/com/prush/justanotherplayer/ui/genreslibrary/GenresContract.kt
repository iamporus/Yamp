package com.prush.justanotherplayer.ui.genreslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Genre
import com.prush.justanotherplayer.model.Track

interface GenresContract {

    interface View : BaseView {

        fun displayAllGenres(genresList: MutableList<Genre>)

        fun displayEmptyLibrary()

        fun getViewActivity(): AppCompatActivity

        fun displayGenreDetails(genre: Genre)

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadLibraryGenres()

        fun loadGenreDetails(selectedTrackPosition: Int)

        fun startGenrePlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int)

        fun onCleanup()
    }
}