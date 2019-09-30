package com.prush.justanotherplayer.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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

interface OnBitmapLoadedListener {
    fun onBitmapLoaded(resource: Bitmap)
    fun onBitmapLoadingFailed()
}

fun loadAlbumArt(context: Context, contentId: Long, imageView: ImageView, errorResource: Int = -1) {

    Glide.with(context)
        .asBitmap()
        .load(getAlbumArtUri(context, contentId))
        .error(errorResource)
        .into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                imageView.setImageBitmap(resource)
            }

        })
}

fun loadAlbumArt(
    context: Context,
    contentId: Long,
    onBitmapLoadedListener: OnBitmapLoadedListener
) {

    Glide.with(context)
        .asBitmap()
        .load(getAlbumArtUri(context, contentId))
        .into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                onBitmapLoadedListener.onBitmapLoaded(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                onBitmapLoadedListener.onBitmapLoadingFailed()
            }

        })
}

fun loadAlbumArt(
    context: Context,
    resourceId: Int,
    onBitmapLoadedListener: OnBitmapLoadedListener
) {
    Glide.with(context)
        .asBitmap()
        .load(resourceId)
        .into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                onBitmapLoadedListener.onBitmapLoaded(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                onBitmapLoadedListener.onBitmapLoadingFailed()
            }

        })
}