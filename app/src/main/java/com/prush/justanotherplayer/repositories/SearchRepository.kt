package com.prush.justanotherplayer.repositories

import android.content.Context

class SearchRepository(
    private val trackRepository: ITrackRepository,
    private val albumRepository: IAlbumRepository,
    private val artistRepository: IArtistRepository
) : ISearchRepository {

    override suspend fun searchAll(context: Context, query: String): SearchResult {

        val searchResult = SearchResult()
        val tracksList = trackRepository.searchTracksByName(context, query)

        searchResult.tracks = tracksList

        //TODO: add support for albums and artists

        return searchResult
    }

    companion object {

        private var INSTANCE: SearchRepository? = null

        @JvmStatic
        fun getInstance(
            trackRepository: ITrackRepository,
            albumRepository: IAlbumRepository,
            artistRepository: IArtistRepository
        ): SearchRepository {
            return INSTANCE ?: SearchRepository(
                trackRepository,
                albumRepository,
                artistRepository
            ).apply {
                INSTANCE = this
            }
        }
    }
}