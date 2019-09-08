package com.prush.justanotherplayer.ui.trackslibrary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import kotlinx.android.synthetic.main.base_recylerview_layout.*

private val TAG = TracksLibraryFragment::class.java.name

class TracksLibraryFragment : BaseRecyclerFragment(), TracksContract.View,
    RecyclerAdapter.OnItemClickListener {

    private lateinit var tracksPresenter: TracksContract.Presenter
    private lateinit var listPresenter: ListPresenter<Track>

    private lateinit var adapter: RecyclerAdapter<Track>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: Inject these guys
        tracksPresenter = TracksPresenter(Injection.provideTrackRepository(), this)
        listPresenter = TracksListPresenter()

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        tracksPresenter.loadLibraryTracks()
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        tracksPresenter.startTrackPlayback(selectedTrackPosition)
    }

    override fun displayLibraryTracks(trackList: MutableList<Track>) {
        Log.d(TAG, "Loaded some tracks")
        listPresenter.setItemsList(trackList, adapter)
    }

    override fun displayEmptyLibrary() {
        showEmptyLibrary()
    }

    override fun onDestroy() {
        super.onDestroy()
        tracksPresenter.onCleanup()
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