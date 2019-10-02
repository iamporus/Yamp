package com.prush.justanotherplayer.ui.trackslibrary

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DiffUtil
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.*
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.OnBitmapLoadedListener
import com.prush.justanotherplayer.utils.loadAlbumArt

open class TracksListPresenter : ListPresenter<Track> {

    override var rowLayoutId: Int = R.layout.track_list_item_row

    override var itemsList: MutableList<Track> = mutableListOf()

    override fun getListHeaderRowLayout(): Int {
        return R.layout.header_list_item_action_row
    }

    override fun isHeaderActionAdded(): Boolean {
        return true
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderActionAdded())
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
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {

        when (itemViewType) {
            RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_ACTION_VIEW.ordinal -> {
                (rowView as HeaderViewHolder).apply {
                    setActionText(context.getString(R.string.shuffle_all))
                    setOnClickListener(position, listener)
                }
            }
            else -> {
                val track = itemsList[position]

                (rowView as TrackItemRow).apply {

                    setTitle(track.title)
                    setSubtitle(track.artistName + " - " + track.albumName)
                    setTrackState(track.state, listener)
                    setOnClickListener(position, listener)
                    setOnContextMenuClickListener(position, listener)
                }

                loadAlbumArt(context, track.albumId, object : OnBitmapLoadedListener {
                    override fun onBitmapLoaded(resource: Bitmap) {
                        rowView.setAlbumArt(resource)
                    }

                    override fun onBitmapLoadingFailed() {
                        rowView.setAlbumArt(
                            AppCompatResources.getDrawable(
                                context,
                                track.defaultAlbumArtRes
                            )!!.toBitmap()
                        )
                    }

                })
            }
        }

    }

    override fun setItemsList(itemsList: MutableList<Track>, adapter: RecyclerAdapter<Track>) {
        if (this.itemsList.isNotEmpty()) {

            //TODO: Fix now playing queue not updating issue
            val trackDiffCallback = TrackDiffCallback(this.itemsList, itemsList)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)

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
            return oldTracksList[oldItemPosition].state == newTracksList[newItemPosition].state
        }

    }
}