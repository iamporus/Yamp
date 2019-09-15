package com.prush.justanotherplayer.services

import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.Track_State
import kotlin.properties.Delegates

class NowPlayingInfo(var id: Long, var index: Int)

class NowPlayingQueue : QueueManager {

    var trackList = mutableListOf<Track>()

    var trackListUnShuffled = mutableListOf<Track>()
    var shuffleEnabled: Boolean = false

    var currentPlayingTrackIndex: Int by Delegates.observable(0) { _, oldValue, newValue ->
        onCurrentPlayingTrackChanged?.invoke(oldValue, newValue)
    }

    var onCurrentPlayingTrackChanged: ((Int, Int) -> Unit)? = { oldIndex, newIndex ->
        trackList.forEachIndexed { index, track ->
            if (index == newIndex)
                track.state = Track_State.PLAYING
            else if (index == oldIndex)
                track.state = Track_State.PLAYED
        }
    }

    var currentPlayingTrackId: Long = 0

    override suspend fun getNowPlayingTracks(): MutableList<Track> {
        return trackList
    }

    override fun setNowPlayingTracks(tracksList: MutableList<Track>, keepCopy: Boolean) {
        trackList.clear()
        trackList.addAll(tracksList)

        if (keepCopy) {
            trackListUnShuffled.clear()
            trackListUnShuffled.addAll(tracksList)
        }
    }

    override fun addTrackToQueue(track: Track) {
        trackList.add(track)
    }

    override fun removeTrackFromQueue(track: Track) {
        trackList.remove(track)
    }

    override fun removeTrackFromQueue(index: Int, track: Track) {
        trackList.removeAt(index)
    }

    override fun addAlbumToQueue(albumTracks: MutableList<Track>) {
        trackList.addAll(albumTracks)
    }

    override fun playNext(track: Track) {
        if (currentPlayingTrackIndex == 0)
            trackList.add(track)
        else
            trackList.add(currentPlayingTrackIndex + 1, track)

    }

    override fun clearNowPlayingQueue() {
        trackList.clear()
    }
}