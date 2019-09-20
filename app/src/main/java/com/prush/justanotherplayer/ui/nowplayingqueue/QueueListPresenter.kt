package com.prush.justanotherplayer.ui.nowplayingqueue

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
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.HeaderViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.model.Track_State
import com.prush.justanotherplayer.ui.trackslibrary.TrackItemRow
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter
import com.prush.justanotherplayer.utils.getAlbumArtUri


class QueueListPresenter(var listener: OnTracksReordered) : TracksListPresenter() {

    override var itemsList: MutableList<Track> = mutableListOf()

    override var rowLayoutId: Int = R.layout.queue_track_list_item_row

    private var nowPlayingPosition: Int = 0

    override fun getHeaderInclusivePosition(position: Int): Int {
        return position
    }

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): BaseViewHolder {
        lateinit var itemView: View

        return when (viewType) {

            RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal -> {
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

        val track = itemsList[position]

        (rowView as TrackItemRow).apply {

            setTitle(track.title)
            setSubtitle(track.artistName + " - " + track.albumName)
            setTrackState(track.state, listener)
            setOnClickListener(position, listener)
        }

        if (track.state == Track_State.PLAYING)
            nowPlayingPosition = position

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

    override fun setItemsList(itemsList: MutableList<Track>, adapter: RecyclerAdapter<Track>) {
        if (this.itemsList.isNotEmpty()) {

            val trackDiffCallback = TrackDiffCallback(this.itemsList, itemsList)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)

            diffResult.dispatchUpdatesTo(adapter)
        } else {

            this.itemsList.addAll(itemsList)
            adapter.notifyDataSetChanged()
        }
    }

    override fun isHeaderAdded(): Boolean {
        return false
    }

    override fun isHeaderActionAdded(): Boolean {
        return false
    }

    override fun onItemMoved(
        fromPosition: Int,
        toPosition: Int,
        adapter: RecyclerAdapter<Track>
    ) {

        itemsList.add(toPosition, itemsList.removeAt(fromPosition))

        if (toPosition <= nowPlayingPosition) {
            itemsList[toPosition].state = Track_State.PLAYED
        } else {
            itemsList[toPosition].state = Track_State.IN_QUEUE
        }

        adapter.notifyDataSetChanged()

        listener.onTracksReordered(itemsList)

    }

}