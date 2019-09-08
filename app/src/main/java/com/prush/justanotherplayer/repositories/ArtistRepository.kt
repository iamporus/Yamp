package com.prush.justanotherplayer.repositories

import android.content.Context
import android.util.Log
import com.prush.justanotherplayer.model.Artist

private const val TAG = "AlbumRepository"

class ArtistRepository(private val albumRepository: IAlbumRepository) : IArtistRepository {

    override suspend fun getAllArtists(context: Context): MutableList<Artist> {
        Log.d(TAG, "Fetching albums from SD Card")

        val artists: MutableSet<Artist> = mutableSetOf()
        val albums = albumRepository.getAllAlbums(context)

        for (album in albums) {
            artists.add(album.getArtist())
        }

        for (artist in artists) {
            artist.albumsList =
                (albums.filter { album -> album.artistId == album.artistId}).toMutableList()
        }

        return artists.toMutableList()
    }

    override suspend fun getArtistById(context: Context, artistId: Long): Artist {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        private var INSTANCE: ArtistRepository? = null

        @JvmStatic
        fun getInstance(repository: IAlbumRepository): ArtistRepository {
            return INSTANCE ?: ArtistRepository(repository).apply {
                INSTANCE = this
            }
        }
    }
}