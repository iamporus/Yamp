package com.prush.justanotherplayer.ui.artistdetails

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder
import com.prush.justanotherplayer.utils.getAlbumArtUri

class ArtistDetailsListPresenter(var onCarousalItemClickListener: OnCarousalItemClickListener) :
    ListPresenter<Track>, RecyclerAdapter.OnItemClickListener {

    override var itemsList: MutableList<Track> = mutableListOf()

    var childItemsList: MutableList<Album> = mutableListOf()

    override var rowLayoutId: Int = R.layout.track_list_item_row

    override fun getItemsCount(): Int {
        return itemsList.size
    }

    override fun getChildRowLayout(): Int {
        return R.layout.horizontal_recyclerview_list_item_row
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return ViewTypeEnum.CAROUSAL_LIST_ITEM_VIEW.ordinal

        return ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal
    }

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int):
            BaseViewHolder {

        lateinit var itemView: View

        return when (viewType) {

            ViewTypeEnum.CAROUSAL_LIST_ITEM_VIEW.ordinal -> {
                itemView = LayoutInflater.from(context).inflate(getChildRowLayout(), parent, false)
                ArtistDetailsCarousalViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
                ArtistDetailsFlatViewHolder(itemView)
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
        val track = itemsList[position]

        when (itemViewType) {
            ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal -> {

                (rowView as ArtistDetailsFlatViewHolder).apply {

                    setTitle(track.title)
                    setSubtitle(track.albumName)

                    Glide.with(context)
                        .asBitmap()
                        .error(track.defaultAlbumArtRes)
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

                        })

                    setOnClickListener(position, listener)
                }
            }
            ViewTypeEnum.CAROUSAL_LIST_ITEM_VIEW.ordinal -> {

                (rowView as ArtistDetailsCarousalViewHolder).apply {

                    setupCarousalTitle(context.getString(R.string.albums))
                    setupLayoutManager()
                    setupAdapter(this@ArtistDetailsListPresenter)
                    setupData(childItemsList)
                }
            }
        }
    }

    override fun setItemsList(itemsList: MutableList<Track>, adapter: RecyclerAdapter<Track>) {

        this.itemsList.clear()
        this.itemsList.addAll(itemsList)
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        onCarousalItemClickListener.onCarousalItemClick(selectedTrackPosition)
    }

    interface OnCarousalItemClickListener {

        fun onCarousalItemClick(selectedItemPosition: Int)
    }

    enum class ViewTypeEnum {

        CAROUSAL_LIST_ITEM_VIEW,
        FLAT_LIST_ITEM_VIEW
    }
}

class ArtistDetailsFlatViewHolder(itemView: View) : TrackViewHolder(itemView)