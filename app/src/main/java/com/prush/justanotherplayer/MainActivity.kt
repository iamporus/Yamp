package com.prush.justanotherplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.TrackRepository

class MainActivity : AppCompatActivity(), IMainActivityView {

    override fun displayEmptyLibrary() {

        print("Oops. You don't have any tracks.")
    }

    override fun displayLibraryTracks(trackList: MutableList<Track>) {

        print("Loaded some tracks")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val trackRepository = TrackRepository()
        val presenter = MainActivityPresenter(this, trackRepository)
        presenter.displayAllTracks()
    }
}
