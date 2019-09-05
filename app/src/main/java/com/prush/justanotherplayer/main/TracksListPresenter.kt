package com.prush.justanotherplayer.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.DiffUtil
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
        holder.markTrackAsPlaying(track.isCurrentlyPlaying)

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

    fun setTrackList(tracksList: MutableList<Track>, adapter: TracksRecyclerAdapter) {

        if (this.tracksList.isNotEmpty()) {

            val trackDiffCallback = TrackDiffCallback(this.tracksList, tracksList)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)

            this.tracksList = tracksList
            diffResult.dispatchUpdatesTo(adapter)
        } else {

            this.tracksList.addAll(tracksList)
            adapter.notifyDataSetChanged()
        }
    }

    inner class TrackDiffCallback(
        private val oldTracksList: MutableList<Track>,
        private val newTracksList: MutableList<Track>
    ) : DiffUtil.Callback() {


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldTracksList[oldItemPosition].id == newTracksList[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldTracksList.size
        }

        override fun getNewListSize(): Int {
            return newTracksList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldTracksList[oldItemPosition] == newTracksList[newItemPosition]
        }

    }
}