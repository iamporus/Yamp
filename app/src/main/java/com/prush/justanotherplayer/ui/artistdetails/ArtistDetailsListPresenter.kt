package com.prush.justanotherplayer.ui.artistdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.*
import com.prush.justanotherplayer.base.RecyclerAdapter.ViewTypeEnum
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.ui.trackslibrary.TrackViewHolder

open class ArtistDetailsListPresenter(var onCarousalItemClickListener: RecyclerAdapter.OnCarousalItemClickListener) :
    ListPresenter<Track>, RecyclerAdapter.OnItemInteractedListener {

    override var itemsList: MutableList<Track> = mutableListOf()

    var childItemsList: MutableList<Album> = mutableListOf()

    override var rowLayoutId: Int = R.layout.album_tracks_list_item_row

    override fun getItemsCount(): Int {
        return itemsList.size + 3
    }

    override fun getChildRowLayout(): Int {
        return R.layout.horizontal_recyclerview_list_item_row
    }

    override fun getListHeaderRowLayout(): Int {
        return R.layout.header_list_item_row
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            getItemsCount() - 1 -> {
                ViewTypeEnum.CAROUSAL_LIST_ITEM_VIEW.ordinal
            }
            0, getItemsCount() - 2 -> {
                ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal
            }
            else -> {
                ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal
            }
        }
    }

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int):
            BaseViewHolder {

        lateinit var itemView: View

        return when (viewType) {

            ViewTypeEnum.CAROUSAL_LIST_ITEM_VIEW.ordinal -> {
                itemView = LayoutInflater.from(context).inflate(getChildRowLayout(), parent, false)
                ArtistDetailsCarousalViewHolder(itemView)
            }
            ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal -> {
                itemView =
                    LayoutInflater.from(context).inflate(getListHeaderRowLayout(), parent, false)
                HeaderViewHolder(itemView)
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
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {

        when (itemViewType) {
            ViewTypeEnum.FLAT_LIST_ITEM_VIEW.ordinal -> {

                (rowView as ArtistDetailsFlatViewHolder).apply {

                    val trackPosition = position - 1
                    val track = itemsList[trackPosition]

                    setTitle(track.title)
                    setSubtitle(track.albumName)
                    setTrackNumber(position)

                    setOnClickListener(trackPosition, listener)
                    setOnContextMenuClickListener(trackPosition, listener)
                }
            }
            ViewTypeEnum.CAROUSAL_LIST_ITEM_VIEW.ordinal -> {

                (rowView as ArtistDetailsCarousalViewHolder).apply {

                    setupLayoutManager()
                    setupAdapter(this@ArtistDetailsListPresenter)
                    setupData(childItemsList)
                }
            }
            ViewTypeEnum.HEADER_LIST_ITEM_VIEW.ordinal -> {

                (rowView as HeaderViewHolder).apply {
                    when (position) {
                        0 -> {
                            setTitle(context.getString(R.string.tracks))
                        }
                        getItemsCount() - 2 -> {
                            setTitle(context.getString(R.string.albums))
                        }
                    }
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

}

class ArtistDetailsFlatViewHolder(itemView: View) : TrackViewHolder(itemView)