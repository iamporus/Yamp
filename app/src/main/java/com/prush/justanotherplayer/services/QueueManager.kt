package com.prush.justanotherplayer.services

import com.prush.justanotherplayer.model.Track

interface QueueManager {

    suspend fun getNowPlayingTracks(): MutableList<Track>

    fun keepUnShuffledTracks(tracksList: MutableList<Track>)

    fun setupQueue(tracksList: MutableList<Track>, enableShuffle: Boolean)

    fun addTrackToQueue(track: Track)

    fun removeTrackFromQueue(track: Track)

    fun removeTrackFromQueue(index: Int, track: Track)

    fun addAlbumToQueue(albumTracks: MutableList<Track>)

    fun playNext(track: Track)

    fun clearNowPlayingQueue()

}