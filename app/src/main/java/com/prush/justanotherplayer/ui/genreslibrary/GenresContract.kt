package com.prush.justanotherplayer.ui.genreslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Genre

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

        fun startGenrePlayback(selectedTrackPosition: Int)

        fun onCleanup()
    }
}