package com.prush.justanotherplayer.services

import com.prush.justanotherplayer.model.Track

class NowPlayingQueue : QueueManager {

    var trackList = mutableListOf<Track>()
    var currentPlayingTrackIndex: Int = 0

    override suspend fun getNowPlayingTracks(): MutableList<Track> {
        return trackList
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