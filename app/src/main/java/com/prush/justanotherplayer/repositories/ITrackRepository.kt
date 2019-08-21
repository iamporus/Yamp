package com.prush.justanotherplayer.repositories

import com.prush.justanotherplayer.model.Track

interface ITrackRepository {

    fun getAllTracks(): MutableList<Track>
}