package com.prush.justanotherplayer.ui.albumdetails

import android.content.Context
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter

class AlbumTracksListPresenter : TracksListPresenter() {

    override var rowLayoutId: Int = R.layout.album_tracks_list_item_row

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {
        val track = itemsList[position]

        (rowView as TrackViewHolder).apply {

            setTitle(track.title)
            setSubtitle(track.albumName)
            setDuration(track.duration)
            setTrackNumber(track.trackNumber)
            setOnClickListener(position, listener)
        }
    }

}