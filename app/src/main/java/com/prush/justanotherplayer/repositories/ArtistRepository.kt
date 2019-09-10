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
                (albums.filter { album -> album.artistId == artist.artistId }).toMutableList()

            for (album in artist.albumsList) {
                val tracks = album.tracksList
                for (track in tracks) {
                    if (track.artistId == artist.artistId)
                        artist.tracksList.add(track)
                }
            }
        }

        return artists.toMutableList()
    }

    override suspend fun getArtistById(context: Context, artistId: Long): Artist {

        val artists = getAllArtists(context)
        val emptyArtist = Artist()

        return artists.find { artist -> artist.artistId == artistId } ?: emptyArtist
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