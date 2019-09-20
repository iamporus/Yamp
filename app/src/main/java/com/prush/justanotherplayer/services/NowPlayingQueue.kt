package com.prush.justanotherplayer.services

import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.Track_State
import kotlin.properties.Delegates

class NowPlayingInfo(var id: Long, var index: Int)

class NowPlayingQueue : QueueManager {

    var nowPlayingTracksList = mutableListOf<Track>()

    var trackListUnShuffled = mutableListOf<Track>()
    var shuffleEnabled: Boolean = false

    var currentPlayingTrackIndex: Int by Delegates.observable(0) { _, oldValue, newValue ->
        onCurrentPlayingTrackChanged?.invoke(oldValue, newValue)
    }

    private var onCurrentPlayingTrackChanged: ((Int, Int) -> Unit)? = { oldIndex, newIndex ->
        nowPlayingTracksList.forEachIndexed { index, track ->
            if (index == newIndex)
                track.state = Track_State.PLAYING
            else if (index == oldIndex)
                track.state = Track_State.PLAYED
        }
    }

    var currentPlayingTrackId: Long = 0

    override suspend fun getNowPlayingTracks(): MutableList<Track> {
        return nowPlayingTracksList
    }

    override fun keepUnShuffledTracks(tracksList: MutableList<Track>) {
        trackListUnShuffled.clear()
        trackListUnShuffled.addAll(tracksList)
    }

    override fun setupQueue(tracksList: MutableList<Track>, enableShuffle: Boolean) {
        nowPlayingTracksList.clear()
        nowPlayingTracksList.addAll(tracksList)
        shuffleEnabled = enableShuffle
    }

    override fun addTrackToQueue(track: Track) {
        nowPlayingTracksList.add(track)
    }

    override fun removeTrackFromQueue(track: Track) {
        nowPlayingTracksList.remove(track)
    }

    override fun removeTrackFromQueue(index: Int, track: Track) {
        nowPlayingTracksList.removeAt(index)
    }

    override fun addAlbumToQueue(albumTracks: MutableList<Track>) {
        nowPlayingTracksList.addAll(albumTracks)
    }

    override fun playNext(track: Track) {
        if (currentPlayingTrackIndex == 0)
            nowPlayingTracksList.add(track)
        else
            nowPlayingTracksList.add(currentPlayingTrackIndex + 1, track)

    }

    override fun clearNowPlayingQueue() {
        nowPlayingTracksList.clear()
    }
}