package com.prush.justanotherplayer.repositories

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.prush.justanotherplayer.model.*

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

    override suspend fun getGenreById(context: Context, genreId: Long): Genre {

        val genre = Genre()

        val genCursor = getGenreByIdQuery(context, genreId)
        when {
            genCursor == null -> {
                throw RuntimeException("Problem with Media Content Provider")
            }
            !genCursor.moveToNext() -> {
                Log.d(TAG, "No genres on SD Card")
                genCursor.close()
            }
            else -> {

                genre.name =
                    genCursor.getString(genCursor.getColumnIndex(MediaStore.Audio.Genres.NAME))
                genCursor.close()
            }
        }

        val trackList = mutableListOf<Track>()
        val cursor: Cursor? = getAllTracksQuery(
            context,
            genre.getTracksByGenreIdUri(genreId)
        )

        when {
            cursor == null -> {
                throw RuntimeException("Problem with Media Content Provider")
            }
            !cursor.moveToNext() -> {
                Log.d(TAG, "No tracks on SD Card")
                cursor.close()
            }
            else -> {

                do {
                    trackList.add(Track(cursor))

                } while (cursor.moveToNext())

                cursor.close()
            }
        }

        genre.tracksList = trackList

        val albumsSet: MutableSet<Album> = mutableSetOf()
        for (track in trackList) {
            albumsSet.add(track.getAlbum())
        }

        genre.albumsList = albumsSet.toMutableList()

        return genre
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