package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.base.*
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.NowPlayingQueue
import kotlinx.android.synthetic.main.base_recylerview_layout.*

private val TAG = QueueFragment::class.java.name

class QueueFragment : BaseRecyclerFragment(), QueueContract.View,
    RecyclerAdapter.OnItemInteractedListener, OnConnectedToService {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var queuePresenter: QueueContract.Presenter
    private lateinit var listPresenter: ListPresenter<Track>

    private lateinit var adapter: RecyclerAdapter<Track>

    override fun onConnectedToService(nowPlayingQueue: NowPlayingQueue) {
        Log.d(TAG, "onConnectedToService... size is ${nowPlayingQueue.trackList.size}")

        queuePresenter = QueuePresenter(nowPlayingQueue, this)
        listPresenter = QueueListPresenter()

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter

        val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(baseRecyclerView)

        queuePresenter.loadNowPlayingTracks()
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        queuePresenter.prepareTrackPlayback(selectedTrackPosition)

    }

    override fun displayNowPlayingTracks(trackList: MutableList<Track>) {
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

    override fun updateNowPlaying() {
        queuePresenter.loadNowPlayingTracks()
    }

    override fun displayEmptyQueue() {
        Log.d(TAG, "Oops found empty")
        showEmptyLibrary()
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onDestroy() {
        super.onDestroy()
        queuePresenter.onCleanup()
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    companion object {
        fun newInstance() = QueueFragment()
    }

}

interface OnConnectedToService {
    fun onConnectedToService(nowPlayingQueue: NowPlayingQueue)
}