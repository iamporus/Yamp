package com.prush.justanotherplayer.repositories

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.getAllTracksQuery
import com.prush.justanotherplayer.model.getTrackByIdQuery
import com.prush.justanotherplayer.model.getTracksByNameQuery

private const val TAG = "TrackRepository"

class TrackRepository : ITrackRepository {

    override suspend fun getTrackById(context: Context, trackId: Long): Track {
        Log.d(TAG, "Fetching track by id $trackId")

        val cursor: Cursor? = getTrackByIdQuery(context, trackId)
        when {
            cursor == null -> {
                throw RuntimeException("Problem with Media Content Provider")
            }
            !cursor.moveToNext() -> {
                Log.d(TAG, "No track with id $trackId found on SD Card")
                cursor.close()
                return Track()
            }
            else -> {

                val track = Track(cursor)
                cursor.close()

                return track
            }
        }
    }

    override suspend fun getAllTracks(context: Context): MutableList<Track> {
        Log.d(TAG, "Fetching tracks from SD Card")

        val trackList: MutableList<Track> = mutableListOf()

        val cursor: Cursor? = getAllTracksQuery(context)

        getTracksListFromCursor(cursor, trackList)

        return trackList
    }

    override suspend fun searchTracksByName(context: Context, query: String): MutableList<Track> {
        Log.d(TAG, "Fetching tracks starting with $query")

        val trackList: MutableList<Track> = mutableListOf()

        val cursor: Cursor? = getTracksByNameQuery(context, query)

        getTracksListFromCursor(cursor, trackList)

        return trackList
    }

    private fun getTracksListFromCursor(
        cursor: Cursor?,
        trackList: MutableList<Track>
    ) {
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
    }

    companion object {

        private var INSTANCE: TrackRepository? = null

        @JvmStatic
        fun getInstance(): TrackRepository {
            return INSTANCE ?: TrackRepository().apply {
                INSTANCE = this
            }
        }
    }
}