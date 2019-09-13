package com.prush.justanotherplayer.base

import android.content.Context
import android.view.ViewGroup

interface ListPresenter<T> {

    var itemsList: MutableList<T>

    var rowLayoutId: Int

    fun getItemsCount(): Int {

        return if (isHeaderAdded() && isHeaderActionAdded())
            itemsList.size + 2
        else if (isHeaderAdded() || isHeaderActionAdded())
            itemsList.size + 1
        else
            itemsList.size
    }

    fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): BaseViewHolder

    fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        itemViewType: Int,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    )

    fun setItemsList(itemsList: MutableList<T>, adapter: RecyclerAdapter<T>)

    fun isHeaderAdded(): Boolean = false

    fun isHeaderActionAdded(): Boolean = false

    fun getListHeaderRowLayout() = -1

    fun getChildRowLayout() = -1

    fun getChildRowLayoutPosition() = -1

    fun getChildItemsCount() = -1

    fun getItemViewType(position: Int) = -1
}