package com.prush.justanotherplayer.repositories

import android.content.Context
import com.prush.justanotherplayer.model.Album

interface IAlbumRepository {

    suspend fun getAllAlbums(context: Context): MutableList<Album>

    suspend fun getAlbumById(context: Context, albumId: Long): Album
}