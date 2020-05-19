package com.prush.justanotherplayer.mediautils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.getAlbumArtUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun getMediaDescriptionForLockScreen(
    context: Context,
    track: Track,
    callback: () -> Unit
): MediaDescriptionCompat {

    val bundle = Bundle()

    var bitmap: Bitmap?

    if (track.albumArtBitmap == null) {

        val bitmapDrawable =
            BitmapFactory.decodeResource(context.resources, track.defaultAlbumArtRes)

        bitmap = bitmapDrawable

        CoroutineScope(IO).launch {
            try {
                val pfd = context.contentResolver
                    .openFileDescriptor(getAlbumArtUri(context, track.albumId), "r")

                if (pfd != null) {
                    val fd = pfd.fileDescriptor
                    bitmap = BitmapFactory.decodeFileDescriptor(fd)
                    pfd.close()

                    track.albumArtBitmap = bitmap

                    withContext(Main) {
                        callback.invoke()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } else {
        bitmap = track.albumArtBitmap
    }

    bundle.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
    bundle.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)

    track.albumArtBitmap = null

    return MediaDescriptionCompat.Builder()
        .setTitle(track.title)
        .setDescription(track.artistName)
        .setSubtitle(track.albumName)
        .setIconBitmap(bitmap)
        .setExtras(bundle)
        .build()

}