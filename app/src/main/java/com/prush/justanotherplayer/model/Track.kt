package com.prush.justanotherplayer.model

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.prush.justanotherplayer.R
import java.io.Serializable

//static fields
private val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

private const val all_tracks_selection =
    MediaStore.Audio.Media.IS_MUSIC + "=1 OR " + MediaStore.Audio.Media.IS_PODCAST + "=1"

private const val track_with_id_selection =
    "(" + all_tracks_selection + ") AND " + MediaStore.Audio.Media._ID + "=?"

private const val track_with_name_selection =
    "(" + all_tracks_selection + ") AND " + MediaStore.Audio.Media.TITLE + " LIKE ?"


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
fun getAllTracksQuery(context: Context): Cursor? {

    return context.contentResolver.query(
        uri,
        projection,
        all_tracks_selection,
        null,
        MediaStore.Audio.Media.TRACK
    )
}

fun getAllTracksQuery(context: Context, uri: Uri): Cursor? {

    return context.contentResolver.query(
        uri,
        projection,
        all_tracks_selection,
        null,
        MediaStore.Audio.Media.TRACK
    )
}

fun getTrackByIdQuery(context: Context, id: Long): Cursor? {

    return context.contentResolver.query(
        uri,
        projection,
        track_with_id_selection,
        arrayOf(id.toString()),
        null
    )
}

fun getTracksByNameQuery(context: Context, query: String): Cursor? {

    return context.contentResolver.query(
        uri,
        projection,
        track_with_name_selection,
        arrayOf("$query%"),
        null
    )
}

enum class Track_State {

    PLAYING,
    PAUSED,
    PLAYED,
    IN_QUEUE
}

open class Track() : Serializable {

    constructor(cursor: Cursor) : this() {

        defaultAlbumArtRes = R.drawable.ic_audiotrack
        id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
        title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
        artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID))
        artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
        albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
        albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
        duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
        trackNumber = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))

        if (trackNumber >= 1000) {
            trackNumber %= 1000
        }

    }

    var id: Long = 0
    var artistId: Long = 0
    var albumId: Long = 0
    var duration: Long = 0
    var title: String = ""
    var artistName: String = ""
    var albumName: String = ""
    var trackNumber: Int = 0
    var defaultAlbumArtRes: Int = 0
    var albumArtBitmap: Bitmap? = null
    var state: Track_State = Track_State.IN_QUEUE

    fun getPlaybackUri(): Uri {

        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    fun getAlbum(): Album {
        return Album.Builder
            .albumId(albumId)
            .albumName(albumName)
            .artistId(artistId)
            .artistName(artistName)
            .build()
    }

    override fun toString(): String {
        return title.slice(0..8)
    }
}
