package com.prush.justanotherplayer.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.utils.PLAY_CONTEXT
import com.prush.justanotherplayer.utils.PLAY_CONTEXT_TYPE
import com.prush.justanotherplayer.utils.SEARCH_QUERY
import com.prush.justanotherplayer.utils.SELECTED_TRACK_POSITION
import kotlinx.android.synthetic.main.base_recylerview_layout.*

private val TAG = SearchFragment::class.java.name

class SearchFragment : BaseRecyclerFragment(), SearchContract.View,
    RecyclerAdapter.OnItemInteractedListener {

    private lateinit var searchPresenter: SearchContract.Presenter
    private lateinit var listPresenter: ListPresenter<Track>
    private var searchQuery: String? = ""

    private lateinit var adapter: RecyclerAdapter<Track>

    override fun getLayoutResource(): Int {
        return R.layout.search_layout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            searchQuery = requireArguments().getString(SearchManager.QUERY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchPresenter = SearchPresenter(Injection.provideSearchRepository(), this)
        listPresenter = SearchListPresenter()

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    fun searchTracks(query: String) {
        searchQuery = query
        searchPresenter.loadTracksStartingWith(query)
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        searchPresenter.prepareTrackPlayback(selectedTrackPosition)

    }

    override fun displayFoundTracks(trackList: MutableList<Track>) {
        Log.d(TAG, "Loaded some tracks")
        emptyLayout.visibility = View.INVISIBLE
        baseRecyclerView.visibility = View.VISIBLE
        listPresenter.setItemsList(trackList, adapter)
    }

    override fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>) {
        val intent = Intent(getViewActivity(), AudioPlayerService::class.java)
        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
        intent.putExtra(PLAY_CONTEXT_TYPE, PLAY_CONTEXT.SEARCH_TRACKS)
        intent.putExtra(SEARCH_QUERY, searchQuery)
        Util.startForegroundService(getViewActivity(), intent)
    }

    override fun displayInfoText() {
        showEmptyLibrary()
    }

    override fun displayEmptyResult() {
        Log.d(TAG, "Oops found empty")
        showEmptyLibrary()
        emptyLayout.text = getString(R.string.no_results_found)
    }

    override fun onContextMenuClicked(position: Int) {
        super.onContextMenuClicked(position)
        searchPresenter.prepareTrackContextMenu(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchPresenter.onCleanup()
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    companion object {

        @JvmStatic
        fun newInstance(query: String): SearchFragment {
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(SearchManager.QUERY, query)
                }
            }
        }
    }

}