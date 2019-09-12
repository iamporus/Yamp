package com.prush.justanotherplayer.services

import com.prush.justanotherplayer.model.Track

class NowPlayingQueue : QueueManager {

    var nowPlayingTracks = mutableListOf<Track>()
    var currentPlayingTrackIndex: Int = 0

    override fun addTrackToQueue(track: Track) {
        nowPlayingTracks.add(track)
    }

    override fun removeTrackFromQueue(track: Track) {
        nowPlayingTracks.remove(track)
    }

    override fun removeTrackFromQueue(index: Int, track: Track) {
        nowPlayingTracks.removeAt(index)
    }

    override fun addAlbumToQueue(albumTracks: MutableList<Track>) {
        nowPlayingTracks.addAll(albumTracks)
    }

    override fun playNext(track: Track) {
        if (currentPlayingTrackIndex == 0)
            nowPlayingTracks.add(track)
        else
            nowPlayingTracks.add(currentPlayingTrackIndex + 1, track)

    }

    override fun clearNowPlayingQueue() {
        nowPlayingTracks.clear()
    }
}