package com.prush.justanotherplayer.repositories

import android.content.Context
import com.prush.justanotherplayer.model.Artist

interface IArtistRepository {

    suspend fun getAllArtists(context: Context): MutableList<Artist>

    suspend fun getArtistById(context: Context, artistId: Long): Artist
}