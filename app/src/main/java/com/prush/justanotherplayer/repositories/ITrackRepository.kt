package com.prush.justanotherplayer.repositories

import com.prush.justanotherplayer.model.Track

interface ITrackRepository {

    suspend fun getAllTracks(): MutableList<Track>
}