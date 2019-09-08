package com.prush.justanotherplayer.ui.albumdetails

import android.content.Context
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter
import com.prush.justanotherplayer.ui.trackslibrary.TracksRowView

class AlbumTracksListPresenter : TracksListPresenter() {

    override var rowLayoutId: Int = R.layout.album_tracks_list_item_row

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: TracksRowView,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {
        val track = itemsList[position]
        rowView.setTrackTitle(track.title)
        rowView.setTrackAlbum(track.albumName)
        rowView.setTrackDuration(track.duration)
        rowView.setOnClickListener(position, listener)
    }

}