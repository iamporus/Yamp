package com.prush.justanotherplayer.repositories

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.prush.justanotherplayer.model.Track

class TrackRepository(private val context: Context) : ITrackRepository {


    override fun getAllTracks(): MutableList<Track> {

        val trackList: MutableList<Track> = mutableListOf()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )

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

                val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)

                do {
                    val songId = cursor.getLong(idColumn)
                    val songTitle = cursor.getString(titleColumn)

                    trackList.add(Track(songId, songTitle))

                } while (cursor.moveToNext())

                cursor.close()
            }
        }

        return trackList
    }
}