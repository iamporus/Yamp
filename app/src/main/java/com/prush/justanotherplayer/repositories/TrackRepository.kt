package com.prush.justanotherplayer.repositories

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.getQuery

class TrackRepository(private val context: Context) : ITrackRepository {


    override fun getAllTracks(): MutableList<Track> {
        Log.d("TrackRepository", "Fetching tracks from SD Card")
        val trackList: MutableList<Track> = mutableListOf()

        val cursor: Cursor? = getQuery(context)

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