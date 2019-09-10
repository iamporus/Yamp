package com.prush.justanotherplayer.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter<T>(
    private val listPresenter: ListPresenter<T>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return listPresenter.getViewHolder(parent.context, parent, viewType)
    }

    override fun getItemCount(): Int {
        return listPresenter.getItemsCount()
    }

    override fun getItemViewType(position: Int): Int {
        return listPresenter.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        listPresenter.onBindTrackRowViewAtPosition(
            holder.itemView.context,
            holder,
            holder.itemViewType,
            position,
            itemClickListener
        )
    }

    enum class ViewTypeEnum {

        CAROUSAL_LIST_ITEM_VIEW,
        FLAT_LIST_ITEM_VIEW,
        HEADER_LIST_ITEM_VIEW
    }

    interface OnItemClickListener {
        fun onItemClick(
            selectedTrackPosition: Int
        )
    }
}