package com.prush.justanotherplayer.base

import android.content.Context
import android.view.ViewGroup

interface ListPresenter<T> {

    var itemsList: MutableList<T>

    var rowLayoutId: Int

    fun getItemsCount(): Int

    fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): BaseViewHolder

    fun onBindTrackRowViewAtPosition(
        context: Context, rowView: ItemRowView, position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    )

    fun setItemsList(itemsList: MutableList<T>, adapter: RecyclerAdapter<T>)
}