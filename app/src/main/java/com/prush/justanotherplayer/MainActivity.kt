package com.prush.justanotherplayer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.TrackRepository

class MainActivity : AppCompatActivity(), IMainActivityView {

    private val TAG = javaClass.name

    override fun displayError() {
        Log.d(TAG, "Oops. Something wrong with the sdcard.")
    }

    override fun displayEmptyLibrary() {

        Log.d(TAG, "Oops. You don't have any tracks.")
    }

    override fun displayLibraryTracks(trackList: MutableList<Track>) {

        Log.d(TAG, "Loaded some tracks")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val trackRepository = TrackRepository(applicationContext)
        val presenter = MainActivityPresenter(this, trackRepository)
        presenter.displayAllTracks()
    }
}
