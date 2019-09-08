package com.prush.justanotherplayer.repositories

import android.content.Context
import com.prush.justanotherplayer.model.Genre

interface IGenreRepository {

    suspend fun getAllGenres(context: Context): MutableList<Genre>

    suspend fun getGenreById(context: Context, trackId: Long): Genre
}