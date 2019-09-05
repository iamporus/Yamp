package com.prush.justanotherplayer.repositories

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.getAllTracksQuery
import com.prush.justanotherplayer.model.getTrackByIdQuery

class TrackRepository(private val context: Context) : ITrackRepository {

    override suspend fun getTrackById(trackId: Long): Track {
        Log.d("TrackRepository", "Fetching track by id $trackId")

        val cursor: Cursor? = getTrackByIdQuery(context, trackId)
        when {
            cursor == null -> {
                throw RuntimeException("Problem with Media Content Provider")
            }
            !cursor.moveToNext() -> {
                Log.d("TrackRepository", "No track with id $trackId found on SD Card")
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

    override suspend fun getAllTracks(): MutableList<Track> {
        Log.d("TrackRepository", "Fetching tracks from SD Card")

        val trackList: MutableList<Track> = mutableListOf()

        val cursor: Cursor? = getAllTracksQuery(context)

        when {
            cursor == null -> {
                throw RuntimeException("Problem with Media Content Provider")
            }
            !cursor.moveToNext() -> {
                Log.d("TrackRepository", "No tracks on SD Card")
                cursor.close()
                return trackList
            }
            else -> {

                do {
                    trackList.add(Track(cursor))

                } while (cursor.moveToNext())

                cursor.close()
            }
        }

        return trackList
    }
}