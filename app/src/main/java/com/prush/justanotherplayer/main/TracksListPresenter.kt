package com.prush.justanotherplayer.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.model.Track

class TracksListPresenter {

    private var tracksList: MutableList<Track> = mutableListOf()

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
        holder.setTrackAlbum(track.artistName + " - " + track.albumName)

        Glide.with(holder.itemView)
            .asBitmap()
            .load(track.getAlbumArtUri(holder.itemView.context))
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.setTrackAlbumArt(resource)
                }

            })

        holder.itemView.setOnClickListener {
            listener.onItemClick(tracksList, position)
        }
    }

    fun setTrackList(tracksList: MutableList<Track>) {
        this.tracksList = tracksList
    }
}