package com.prush.justanotherplayer.ui.artistslibrary

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DiffUtil
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.utils.OnBitmapLoadedListener
import com.prush.justanotherplayer.utils.loadAlbumArt

class ArtistsListPresenter : ListPresenter<Artist> {

    override var rowLayoutId: Int = R.layout.artist_list_item_row

    override var itemsList: MutableList<Artist> = mutableListOf()

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int)
            : BaseViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
        return ArtistViewHolder(itemRowView)
    }

    override fun getItemsCount(): Int {
        return itemsList.size
    }

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        itemViewType: Int,
        position: Int,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {

        val artist = itemsList[position]
        rowView.setTitle(artist.artistName)
        rowView.setOnClickListener(position, listener)

        loadAlbumArt(context, artist.defaultAlbumArtRes, object : OnBitmapLoadedListener {
            override fun onBitmapLoaded(resource: Bitmap) {
                rowView.setAlbumArt(resource)
            }

            override fun onBitmapLoadingFailed() {
                rowView.setAlbumArt(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_artist
                    )!!.toBitmap()
                )
            }

        })

    }

    override fun setItemsList(itemsList: MutableList<Artist>, adapter: RecyclerAdapter<Artist>) {
        if (this.itemsList.isNotEmpty()) {

            val trackDiffCallback = ArtistDiffCallback(this.itemsList, itemsList)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)

            diffResult.dispatchUpdatesTo(adapter)
        } else {

            this.itemsList.addAll(itemsList)
            adapter.notifyDataSetChanged()
        }
    }

    inner class ArtistDiffCallback(
        private val oldArtistList: MutableList<Artist>,
        private val newArtistList: MutableList<Artist>
    ) : DiffUtil.Callback() {


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldArtistList[oldItemPosition].artistId == newArtistList[newItemPosition].artistId
        }

        override fun getOldListSize(): Int {
            return oldArtistList.size
        }

        override fun getNewListSize(): Int {
            return newArtistList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }

    }
}