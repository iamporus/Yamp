package com.prush.justanotherplayer.model

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.prush.justanotherplayer.R
import java.io.Serializable

//static fields
private val uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

private val projection = arrayOf(
    MediaStore.Audio.Genres._ID,
    MediaStore.Audio.Genres.NAME
)

//static method
fun getAllGenresQuery(context: Context): Cursor? {

    return context.contentResolver.query(
        uri,
        projection,
        null,
        null,
        MediaStore.Audio.Genres.DEFAULT_SORT_ORDER
    )
}

fun getGenreByIdQuery(context: Context, genreId: Long): Cursor? {

    return context.contentResolver.query(
        uri,
        projection,
        MediaStore.Audio.Genres._ID + "=?",
        arrayOf(genreId.toString()),
        MediaStore.Audio.Genres.DEFAULT_SORT_ORDER
    )
}

open class Genre() : Serializable {

    constructor(cursor: Cursor) : this() {

        defaultAlbumArtRes = R.drawable.ic_empty_library
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Genres._ID))
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME))

    }

    var id: Long = 0
    var name: String = ""
    var defaultAlbumArtRes: Int = 0
    var tracksList: MutableList<Track> = mutableListOf()
    var albumsList: MutableList<Album> = mutableListOf()

    fun getTracksByGenreIdUri(genreId: Long): Uri {

        return MediaStore.Audio.Genres.Members.getContentUri("external", genreId)
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as Genre).id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}