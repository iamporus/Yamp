package com.prush.justanotherplayer.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.getAlbumArtUri


fun getMediaDescriptionForLockScreen(
    context: Context,
    track: Track,
    callback: () -> Unit
): MediaDescriptionCompat {

    Log.d("AudioPlayerService", "getMediaDescriptionForLockScreen called")
    val bundle = Bundle()

    val bitmapDrawable =
        context.resources.getDrawable(track.defaultAlbumArtRes) as BitmapDrawable

    var bitmap: Bitmap? = bitmapDrawable.bitmap

    try {
        val pfd = context.contentResolver
            .openFileDescriptor(getAlbumArtUri(context, track.albumId), "r")

        if (pfd != null) {
            val fd = pfd.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fd)
            pfd.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


    //TODO: fire off async loading of album art
//    Glide.with(context)
//        .asBitmap()
//        .load(track.getAlbumArtUri(context))
//        .into(object : CustomTarget<Bitmap>() {
//            override fun onLoadCleared(placeholder: Drawable?) {
//            }
//
//            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                Log.d(
//                    "AudioPlayerService",
//                    "getMediaDescriptionForLockScreen -- resource ready zala re..."
//                )
//                track.albumArtBitmap = resource
//                callback.invoke()
//            }
//
//        })

    bundle.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
    bundle.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)


    return MediaDescriptionCompat.Builder()
        .setTitle(track.title)
        .setDescription(track.artistName)
        .setSubtitle(track.albumName)
        .setIconBitmap(bitmap)
        .setExtras(bundle)
        .build()

}