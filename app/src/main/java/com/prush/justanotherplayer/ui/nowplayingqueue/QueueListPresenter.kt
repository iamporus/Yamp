package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DiffUtil
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
import com.prush.justanotherplayer.utils.OnBitmapLoadedListener
import com.prush.justanotherplayer.utils.loadAlbumArt


class QueueViewHolder(itemView: View) : TrackViewHolder(itemView) {

    override fun setTrackState(
        state: Track_State,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {
        val container: ViewGroup = itemView.findViewById(R.id.rowLayout)

        val handleImageView: ImageView? = itemView.findViewById(R.id.dragHandleImageView)

        when (state) {

            Track_State.PLAYING, Track_State.PAUSED -> {
                container.alpha = 1f
                handleImageView?.visibility = View.INVISIBLE
                setOnTouchListener(null)

                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(R.color.colorAccent, R.color.toolbarIconColor)
                )
                gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
                container.background = gradientDrawable
            }
            Track_State.IN_QUEUE, Track_State.PLAYED -> {
                container.alpha = 1f
                container.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.queue_selector_ripple)
                handleImageView?.visibility = View.VISIBLE
                setOnTouchListener(listener)
            }
        }
    }
}

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
                QueueViewHolder(itemView)
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

        loadAlbumArt(context, track.albumId, object : OnBitmapLoadedListener {
            override fun onBitmapLoaded(resource: Bitmap) {
                rowView.setAlbumArt(resource)
            }

            override fun onBitmapLoadingFailed() {
                rowView.setAlbumArt(
                    AppCompatResources.getDrawable(context, track.defaultAlbumArtRes)!!.toBitmap()
                )
            }

        })
    }

    override fun setItemsList(itemsList: MutableList<Track>, adapter: RecyclerAdapter<Track>) {
        if (this.itemsList.isNotEmpty()) {

            val trackDiffCallback = QueueTrackDiffCallback(this.itemsList, itemsList)
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

        if (fromPosition >= 0 && toPosition >= 0) {
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

    inner class QueueTrackDiffCallback(
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