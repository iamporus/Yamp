package com.prush.justanotherplayer.ui.artistslibrary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.model.Track

interface ArtistsContract {

    interface View : BaseView {

        fun displayAllArtists(artistList: MutableList<Artist>)

        fun displayEmptyLibrary()

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun loadLibraryArtists()

        fun loadArtistDetails(artistId: Long)

        fun startArtistPlayback(tracksList: MutableList<Track>, selectedTrackPosition: Int)

        fun onCleanup()
    }
}