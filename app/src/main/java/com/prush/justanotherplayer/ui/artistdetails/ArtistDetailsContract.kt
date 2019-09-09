package com.prush.justanotherplayer.ui.artistdetails

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseView
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.model.Track

interface ArtistDetailsContract {

    interface View : BaseView {

        fun displayArtistDetails(artist: Artist)

        fun displayAlbumDetails(album: Album)

        fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>)

        fun getViewActivity(): AppCompatActivity

        fun getApplicationContext(): Context
    }

    interface Presenter {

        fun fetchArtistDetails(artistId: Long): Artist

        fun loadAlbumDetails(album: Album)

        fun prepareTrackPlayback(selectedTrackPosition: Int)

        fun onCleanup()
    }
}