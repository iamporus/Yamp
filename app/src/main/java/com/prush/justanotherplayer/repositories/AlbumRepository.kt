package com.prush.justanotherplayer.repositories

import android.content.Context
import android.util.Log
import com.prush.justanotherplayer.model.Album

private const val TAG = "AlbumRepository"

class AlbumRepository(private val trackRepository: ITrackRepository) : IAlbumRepository {

    override suspend fun getAllAlbums(context: Context): MutableList<Album> {
        Log.d(TAG, "Fetching albums from SD Card")

        val albums: MutableSet<Album> = mutableSetOf()
        val tracks = trackRepository.getAllTracks(context)

        for (track in tracks) {
            albums.add(track.getAlbum())
        }

        for (album in albums) {
            album.tracksList =
                (tracks.filter { track -> track.albumId == album.albumId }).toMutableList()

        }

        return albums.toMutableList()
    }

    override suspend fun getAlbumById(context: Context, albumId: Long): Album {

        val albums = getAllAlbums(context)

        val emptyAlbum = Album()

        return albums.find { album -> album.albumId == albumId } ?: return emptyAlbum
    }

    companion object {

        private var INSTANCE: AlbumRepository? = null

        @JvmStatic
        fun getInstance(trackRepository: ITrackRepository): AlbumRepository {
            return INSTANCE ?: AlbumRepository(trackRepository).apply {
                INSTANCE = this
            }
        }
    }
}