package com.prush.justanotherplayer.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException

fun getAlbumArtUri(context: Context, albumId: Long): Uri {

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