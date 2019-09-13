package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.HeaderViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter

class QueueListPresenter : TracksListPresenter() {

    override var itemsList: MutableList<Track> = mutableListOf()

    override var rowLayoutId: Int = R.layout.track_list_item_row

    override fun getListHeaderRowLayout(): Int {
        return R.layout.header_list_item_row
    }

    override fun getItemViewType(position: Int): Int {

        return when (position) {

            0 -> RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal
            else -> RecyclerAdapter.ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal
        }
    }

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): BaseViewHolder {
        lateinit var itemView: View

        return when (viewType) {

            RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal -> {
                itemView =
                    LayoutInflater.from(context).inflate(getListHeaderRowLayout(), parent, false)
                HeaderViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
                TrackViewHolder(itemView)
            }
        }
    }


    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        itemViewType: Int,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {

        if (itemViewType == RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal) {

            (rowView as HeaderViewHolder).apply {
                when (position) {
                    -1 -> {
                        setTitle(context.getString(R.string.up_next))
                    }
                }
            }
        } else
            super.onBindTrackRowViewAtPosition(context, rowView, itemViewType, position, listener)
    }

    override fun isHeaderAdded(): Boolean {
        return true
    }

    override fun isHeaderActionAdded(): Boolean {
        return false
    }

}