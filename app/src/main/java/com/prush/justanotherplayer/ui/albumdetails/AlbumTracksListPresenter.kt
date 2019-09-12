package com.prush.justanotherplayer.ui.albumdetails

import android.content.Context
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.HeaderViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter

class AlbumTracksListPresenter : TracksListPresenter() {

    override var rowLayoutId: Int = R.layout.album_tracks_list_item_row

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        itemViewType: Int,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {
        val trackPosition = if (isHeaderAdded()) position - 1 else position

        when (itemViewType) {
            RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_ACTION_VIEW.ordinal -> {
                (rowView as HeaderViewHolder).apply {
                    setOnClickListener(trackPosition, listener)
                }
            }
            else -> {
                val track = itemsList[trackPosition]

                (rowView as TrackViewHolder).apply {

                    setTitle(track.title)
                    setSubtitle(track.albumName)
                    setDuration(track.duration)
                    setTrackNumber(track.trackNumber)
                    setOnClickListener(trackPosition, listener)
                }
            }
        }

    }

}