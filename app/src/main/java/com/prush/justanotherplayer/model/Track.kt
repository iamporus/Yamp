package com.prush.justanotherplayer.model

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.io.Serializable

//static fields
private val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

private const val selection =
    MediaStore.Audio.Media.IS_MUSIC + "=1 OR " + MediaStore.Audio.Media.IS_PODCAST + "=1"

private val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.ARTIST_ID,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.ALBUM_ID,
    MediaStore.Audio.Media.ALBUM,
    MediaStore.Audio.Media.DURATION,
    MediaStore.Audio.Media.YEAR,
    MediaStore.Audio.Media.TRACK,
    MediaStore.Audio.Media.DATE_ADDED,
    MediaStore.Audio.Media.IS_PODCAST
)

//static method
fun getQuery(context: Context): Cursor? {

    return context.contentResolver.query(
        uri,
        projection,
        selection,
        null,
        MediaStore.Audio.Media.TRACK
    )
}

open class Track(cursor: Cursor) : Serializable {

    var id: Long = 0
    var artistId: Long = 0
    var albumId: Long = 0
    var duration: Long = 0
    var title: String
    var artistName: String
    var albumName: String

    init {
        id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
        title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
        artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID))
        artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
        albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
        albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
        duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
    }

    fun getPlaybackUri(): Uri {

        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    fun getAlbumArtUri(context: Context): Uri {

        val contentUri =
            ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)

        val cursor = context.contentResolver
            .query(
                contentUri,
                arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                null,
                null,
                null
            )

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    val file =
                        File(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)))
                    if (file.exists()) {
                        try {
                            return Uri.fromFile(file)
                        } catch (ignored: FileNotFoundException) {
                        }
                    }
                }
            } catch (ignored: NullPointerException) {
            } finally {
                cursor.close()
            }
        }

        return Uri.EMPTY
    }
}