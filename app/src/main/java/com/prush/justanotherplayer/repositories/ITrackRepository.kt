package com.prush.justanotherplayer.repositories

import android.content.Context
import com.prush.justanotherplayer.model.Track

interface ITrackRepository {

    suspend fun getAllTracks(context: Context): MutableList<Track>

    suspend fun getTrackById(context: Context, trackId: Long): Track
}