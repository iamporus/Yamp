package com.prush.justanotherplayer.trackslibrary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.main.BaseRecyclerFragment
import com.prush.justanotherplayer.model.Track
import kotlinx.android.synthetic.main.base_recylerview_layout.*

private val TAG = TracksLibraryFragment::class.java.name

class TracksLibraryFragment : BaseRecyclerFragment(), TracksContract.View,
    TracksRecyclerAdapter.OnItemClickListener {

    override lateinit var tracksPresenter: TracksContract.Presenter

    private lateinit var adapter: TracksRecyclerAdapter
    private lateinit var tracksListPresenter: TracksListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tracksListPresenter = TracksListPresenter()
        adapter = TracksRecyclerAdapter(tracksListPresenter, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseRecyclerView.adapter = adapter
    }

    override fun onItemClick(tracksList: MutableList<Track>, selectedTrackPosition: Int) {
        tracksPresenter.startTrackPlayback(tracksList, selectedTrackPosition)
    }

    override fun displayLibraryTracks(trackList: MutableList<Track>) {
        Log.d(TAG, "Loaded some tracks")
        tracksListPresenter.setTrackList(trackList, adapter)
    }

    override fun displayEmptyLibrary() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    companion object {
        fun newInstance() = TracksLibraryFragment()
    }
}