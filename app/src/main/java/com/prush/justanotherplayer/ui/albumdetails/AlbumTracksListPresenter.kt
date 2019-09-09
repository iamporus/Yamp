package com.prush.justanotherplayer.ui.albumdetails

import android.content.Context
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter
import com.prush.justanotherplayer.base.ItemRowView

class AlbumTracksListPresenter : TracksListPresenter() {

    override var rowLayoutId: Int = R.layout.album_tracks_list_item_row

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {
        val track = itemsList[position]
        rowView.setTitle(track.title)
        rowView.setSubtitle(track.albumName)
        rowView.setDuration(track.duration)
        rowView.setOnClickListener(position, listener)
    }

}