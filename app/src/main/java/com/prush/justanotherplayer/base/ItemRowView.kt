package com.prush.justanotherplayer.base

import android.graphics.Bitmap

interface ItemRowView {

    fun setTitle(title: String)

    fun setSubtitle(subtitle: String)

    fun setAlbumArt(resource: Bitmap)

    fun setOnContextMenuClickListener(position: Int, listener: RecyclerAdapter.OnItemInteractedListener)

    fun setOnClickListener(position: Int, listener: RecyclerAdapter.OnItemInteractedListener)

    fun setOnTouchListener(listener: RecyclerAdapter.OnItemInteractedListener?)
}