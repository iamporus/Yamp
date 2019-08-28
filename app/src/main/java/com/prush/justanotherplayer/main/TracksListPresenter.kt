package com.prush.justanotherplayer.main

import com.prush.justanotherplayer.model.Track

class TracksListPresenter {

    private lateinit var tracksList: MutableList<Track>

    fun getTrackListCount(): Int {
        return tracksList.size
    }

    fun onBindTrackRowViewAtPosition(
        holder: TracksRecyclerAdapter.TracksViewHolder,
        position: Int,
        listener: TracksRecyclerAdapter.OnItemClickListener
    ) {
        val track = tracksList[position]
        holder.setTrackTitle(track.title)

        holder.itemView.setOnClickListener {
            listener.onItemClick(track)
        }
    }

    fun setTrackList(trackList: MutableList<Track>) {
        tracksList = trackList
    }
}