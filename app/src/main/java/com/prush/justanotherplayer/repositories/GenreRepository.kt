package com.prush.justanotherplayer.repositories

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.prush.justanotherplayer.model.Genre
import com.prush.justanotherplayer.model.getAllGenresQuery

private const val TAG = "GenreRepository"

class GenreRepository : IGenreRepository {

    override suspend fun getAllGenres(context: Context): MutableList<Genre> {
        Log.d(TAG, "Fetching genres from SD Card")

        val genreList: MutableList<Genre> = mutableListOf()

        val cursor: Cursor? = getAllGenresQuery(context)

        when {
            cursor == null -> {
                throw RuntimeException("Problem with Media Content Provider")
            }
            !cursor.moveToNext() -> {
                Log.d(TAG, "No tracks with genres found on SD Card")
                cursor.close()
                return genreList
            }
            else -> {

                do {
                    genreList.add(Genre(cursor))

                } while (cursor.moveToNext())

                cursor.close()
            }
        }

        return genreList
    }

    override suspend fun getGenreById(context: Context, trackId: Long): Genre {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        private var INSTANCE: GenreRepository? = null

        @JvmStatic
        fun getInstance(): GenreRepository {
            return INSTANCE ?: GenreRepository().apply {
                INSTANCE = this
            }
        }
    }
}