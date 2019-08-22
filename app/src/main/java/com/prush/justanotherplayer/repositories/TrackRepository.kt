package com.prush.justanotherplayer.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.prush.justanotherplayer.model.Track

class TrackRepository(private val context: Context) : ITrackRepository {

    @SuppressLint("Recycle")
    override fun getAllTracks(): MutableList<Track> {

        val trackList: MutableList<Track> = mutableListOf()

        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val cursor: Cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        ) ?: throw RuntimeException("Problem with Media Content Provider")

        val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)

        do {
            val songId = cursor.getLong(idIndex)
            val songTitle = cursor.getString(titleIndex)

            trackList.add(Track(songId, songTitle))

        } while (cursor.moveToNext())

        cursor.close()

        return trackList
    }
}