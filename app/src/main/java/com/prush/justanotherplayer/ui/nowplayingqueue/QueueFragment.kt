package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ItemTouchHelperCallback
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.services.NowPlayingQueue
import com.prush.justanotherplayer.services.TRACKS_LIST
import kotlinx.android.synthetic.main.base_recylerview_layout.*

private val TAG = QueueFragment::class.java.name

class QueueFragment : BaseRecyclerFragment(), QueueContract.View,
    RecyclerAdapter.OnItemInteractedListener, OnConnectedToService, OnTracksReordered {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var queuePresenter: QueueContract.Presenter
    private lateinit var listPresenter: ListPresenter<Track>

    private lateinit var adapter: RecyclerAdapter<Track>

    override fun onConnectedToService(nowPlayingQueue: NowPlayingQueue) {
        Log.d(TAG, "onConnectedToService... size is ${nowPlayingQueue.nowPlayingTracksList.size}")

        queuePresenter = QueuePresenter(nowPlayingQueue, this)
        listPresenter = QueueListPresenter(this)

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

    override fun onTracksReordered(itemsList: MutableList<Track>) {

        val intent = Intent(activity, AudioPlayerService::class.java)
        intent.apply {
            action = AudioPlayerService.PlaybackControls.RE_ORDER.name
            putExtra(TRACKS_LIST, ArrayList(itemsList))
        }
        Util.startForegroundService(activity, intent)
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

interface OnTracksReordered {

    fun onTracksReordered(itemsList: MutableList<Track>)
}