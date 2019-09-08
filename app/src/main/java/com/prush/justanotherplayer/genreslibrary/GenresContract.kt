package com.prush.justanotherplayer.genreslibrary

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

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadLibraryGenres()

        fun loadGenreDetails(genreId: Long)

        fun startGenrePlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int)

        fun onCleanup()
    }
}