package com.prush.justanotherplayer.services

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.prush.justanotherplayer.model.Track

class MediaSourceListUpdateCallback(
    private val concatenatingMediaSource: ConcatenatingMediaSource
) :
    ListUpdateCallback {

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        concatenatingMediaSource.moveMediaSource(fromPosition, toPosition)
    }

    override fun onInserted(position: Int, count: Int) {
    }
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onRemoved(position: Int, count: Int) {}
}

class MediaSourceDiffUtilCallback(
    private val oldTracksList: MutableList<Track>,
    private val newTracksList: MutableList<Track>
) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTracksList[oldItemPosition].id == newTracksList[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldTracksList.size
    }

    override fun getNewListSize(): Int {
        return newTracksList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }

}