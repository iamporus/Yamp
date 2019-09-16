package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.*
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.ui.trackslibrary.TrackItemRow
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder
import com.prush.justanotherplayer.ui.trackslibrary.TracksListPresenter
import com.prush.justanotherplayer.utils.getAlbumArtUri
import java.util.*


class QueueListPresenter : TracksListPresenter() {

    override var itemsList: MutableList<Track> = mutableListOf()

    override var rowLayoutId: Int = R.layout.queue_track_list_item_row

    override fun getListHeaderRowLayout(): Int {
        return R.layout.header_list_item_row
    }

    override fun getItemViewType(position: Int): Int {

        return when (position) {

            0 -> RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal
            else -> RecyclerAdapter.ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal
        }
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

        if (itemViewType == RecyclerAdapter.ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal) {

            (rowView as HeaderViewHolder).apply {
                when (position) {
                    -1 -> {
                        setTitle(context.getString(R.string.up_next))
                    }
                }
            }
        } else {

            val track = itemsList[position]

            (rowView as TrackItemRow).apply {

                setTitle(track.title)
                setSubtitle(track.artistName + " - " + track.albumName)
                setTrackState(track.state)
                setOnClickListener(position, listener)
                setOnLongPressListener(listener)
                setOnTouchListener(listener)
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

    override fun isHeaderAdded(): Boolean {
        return true
    }

    override fun isHeaderActionAdded(): Boolean {
        return false
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition - 1) {
                Collections.swap(itemsList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition) {
                Collections.swap(itemsList, i, i - 1)
            }
        }
    }

}