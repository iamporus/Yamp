package com.prush.justanotherplayer.repositories

import com.prush.justanotherplayer.model.Track

class TrackRepository : ITrackRepository {

    override fun getAllTracks(): MutableList<Track> {
        return mutableListOf()
    }
}