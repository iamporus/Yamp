package com.prush.justanotherplayer.repositories

import android.content.Context
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.model.Track

class SearchResult {
    var tracks: MutableList<Track> = mutableListOf()
    var artists: MutableList<Artist> = mutableListOf()
    var albums: MutableList<Album> = mutableListOf()
}

interface ISearchRepository {

    suspend fun searchAll(context: Context, query: String): SearchResult
}