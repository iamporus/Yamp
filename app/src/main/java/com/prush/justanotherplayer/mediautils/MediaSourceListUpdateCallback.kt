package com.prush.justanotherplayer.mediautils

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
        //TODO: Fix IndexOutOfBounds -> Play any random track from tracks list. Go to now playing and enable shuffle.
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