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

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        listPresenter.onBindTrackRowViewAtPosition(
            holder.itemView.context,
            holder,
            position,
            itemClickListener
        )
    }

    interface OnItemClickListener {
        fun onItemClick(
            selectedTrackPosition: Int
        )
    }
}