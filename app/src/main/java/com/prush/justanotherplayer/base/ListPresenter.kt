package com.prush.justanotherplayer.base

import android.content.Context
import com.prush.justanotherplayer.trackslibrary.TracksRowView

interface ListPresenter<T> {

    var itemsList: MutableList<T>

    var rowLayoutId: Int

    fun getItemsCount(): Int

    fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: TracksRowView,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    )

    fun setItemsList(
        itemsList: MutableList<T>,
        adapter: RecyclerAdapter<T>
    )
}