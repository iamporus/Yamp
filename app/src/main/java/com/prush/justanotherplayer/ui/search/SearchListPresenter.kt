package com.prush.justanotherplayer.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter

class SearchListPresenter : TracksListPresenter() {

    override fun getHeaderInclusivePosition(position: Int): Int {
        return position
    }

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(rowLayoutId, parent, false)

        return TrackViewHolder(itemView)
    }

    override fun setItemsList(itemsList: MutableList<Track>, adapter: RecyclerAdapter<Track>) {

        this.itemsList.clear()
        this.itemsList.addAll(itemsList)
        adapter.notifyDataSetChanged()
    }

    override fun isHeaderAdded(): Boolean {
        return false
    }

    override fun isHeaderActionAdded(): Boolean {
        return false
    }

}