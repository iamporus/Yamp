package com.prush.justanotherplayer.ui.trackslibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.getAlbumArtUri

open class TracksListPresenter : ListPresenter<Track> {

    override var rowLayoutId: Int = R.layout.track_list_item_row

    override var itemsList: MutableList<Track> = mutableListOf()

    override fun getItemsCount(): Int {
        return itemsList.size
    }

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int):
            BaseViewHolder {
        val itemView = LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {
        val track = itemsList[position]

        (rowView as TrackItemRow).apply {

            setTitle(track.title)
            setSubtitle(track.artistName + " - " + track.albumName)
            markAsNowPlaying(track.isCurrentlyPlaying)
            setOnClickListener(position, listener)
        }

        Glide.with(context)
            .asBitmap()
            .load(getAlbumArtUri(context, track.albumId))
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    rowView.setAlbumArt(resource)
                }

            })

    }

    override fun setItemsList(itemsList: MutableList<Track>, adapter: RecyclerAdapter<Track>) {
        if (this.itemsList.isNotEmpty()) {

            val trackDiffCallback = TrackDiffCallback(this.itemsList, itemsList)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)

            this.itemsList = itemsList
            diffResult.dispatchUpdatesTo(adapter)
        } else {

            this.itemsList.addAll(itemsList)
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
            return oldTracksList[oldItemPosition].isCurrentlyPlaying == newTracksList[newItemPosition].isCurrentlyPlaying
        }

    }
}