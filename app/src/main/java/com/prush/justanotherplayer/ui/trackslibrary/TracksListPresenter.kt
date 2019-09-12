package com.prush.justanotherplayer.ui.trackslibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.*
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.getAlbumArtUri

open class TracksListPresenter : ListPresenter<Track> {

    override var rowLayoutId: Int = R.layout.track_list_item_row

    override var itemsList: MutableList<Track> = mutableListOf()

    override fun getListHeaderRowLayout(): Int {
        return R.layout.header_list_item_action_row
    }

    override fun isHeaderAdded(): Boolean {
        return true
    }

    override fun getItemsCount(): Int {

        return if (isHeaderAdded())
            itemsList.size + 1
        else
            itemsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderAdded())
            when (position) {

                0 -> RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_ACTION_VIEW.ordinal
                else -> RecyclerAdapter.ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal
            }
        else
            RecyclerAdapter.ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal
    }

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int):
            BaseViewHolder {

        lateinit var itemView: View

        return when (viewType) {

            RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_ACTION_VIEW.ordinal -> {
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

        val trackPosition = if (isHeaderAdded()) position - 1 else position

        when (itemViewType) {
            RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_ACTION_VIEW.ordinal -> {
                (rowView as HeaderViewHolder).apply {
                    setOnClickListener(trackPosition, listener)
                }
            }
            else -> {
                val track = itemsList[trackPosition]

                (rowView as TrackItemRow).apply {

                    setTitle(track.title)
                    setSubtitle(track.artistName + " - " + track.albumName)
                    markAsNowPlaying(track.isCurrentlyPlaying)
                    setOnClickListener(trackPosition, listener)
                }

                Glide.with(context)
                    .asBitmap()
                    .load(getAlbumArtUri(context, track.albumId))
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            rowView.setAlbumArt(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            rowView.setAlbumArt(
                                BitmapFactory.decodeResource(
                                    context.resources,
                                    R.drawable.playback_track_icon
                                )
                            )
                        }

                    })

            }
        }


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