package com.prush.justanotherplayer.ui.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ItemTouchHelperCallback
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter
import kotlinx.android.synthetic.main.base_recylerview_layout.*

private val TAG = SearchFragment::class.java.name

class SearchFragment : BaseRecyclerFragment(), SearchContract.View,
    RecyclerAdapter.OnItemInteractedListener {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var searchPresenter: SearchContract.Presenter
    private lateinit var listPresenter: ListPresenter<Track>
    private var searchQuery: String? = ""

    private lateinit var adapter: RecyclerAdapter<Track>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            searchQuery = arguments!!.getString(SEARCH_QUERY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchPresenter = SearchPresenter(Injection.provideSearchRepository(), this)
        listPresenter = TracksListPresenter()

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter

        val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(baseRecyclerView)

        searchPresenter.loadTracksStartingWith(searchQuery ?: "")
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        searchPresenter.prepareTrackPlayback(selectedTrackPosition)

    }

    override fun displayFoundTracks(trackList: MutableList<Track>) {
        Log.d(TAG, "Loaded some tracks")
        listPresenter.setItemsList(trackList, adapter)
    }

    override fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>) {
//        val intent = Intent(getViewActivity(), AudioPlayerService::class.java)
//        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
//        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
//        intent.putExtra(TRACKS_LIST, ArrayList(tracksList))
//        Util.startForegroundService(getViewActivity(), intent)
    }


    override fun displayEmptyResult() {
        Log.d(TAG, "Oops found empty")
        showEmptyLibrary()
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
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

        const val SEARCH_QUERY = "SearchQuery"

        fun newInstance(query: String): SearchFragment {
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(SEARCH_QUERY, query)
                }
            }
        }
    }

}