package com.prush.justanotherplayer.ui.albumslibrary

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
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.utils.OnBitmapLoadedListener
import com.prush.justanotherplayer.utils.loadAlbumArt

open class AlbumsListPresenter : ListPresenter<Album> {

    override var rowLayoutId: Int = R.layout.album_list_item_row

    override var itemsList: MutableList<Album> = mutableListOf()

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int)
            : BaseViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
        return AlbumViewHolder(itemRowView)
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

        val album = itemsList[position]
        rowView.setTitle(album.albumName)
        rowView.setOnClickListener(position, listener)

        loadAlbumArt(context, album.albumId, object : OnBitmapLoadedListener {
            override fun onBitmapLoaded(resource: Bitmap) {
                rowView.setAlbumArt(resource)
            }

            override fun onBitmapLoadingFailed() {
                rowView.setAlbumArt(
                    AppCompatResources.getDrawable(context, album.defaultAlbumArtRes)!!.toBitmap()
                )
            }

        })
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        holder.cleanup()
    }

    override fun setItemsList(itemsList: MutableList<Album>, adapter: RecyclerAdapter<Album>) {
        if (this.itemsList.isNotEmpty()) {

            val trackDiffCallback = AlbumDiffCallback(this.itemsList, itemsList)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)

            diffResult.dispatchUpdatesTo(adapter)
        } else {

            this.itemsList.addAll(itemsList)
            adapter.notifyDataSetChanged()
        }
    }

    inner class AlbumDiffCallback(
        private val oldAlbumList: MutableList<Album>,
        private val newAlbumList: MutableList<Album>
    ) : DiffUtil.Callback() {


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldAlbumList[oldItemPosition].albumId == newAlbumList[newItemPosition].albumId
        }

        override fun getOldListSize(): Int {
            return oldAlbumList.size
        }

        override fun getNewListSize(): Int {
            return newAlbumList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }

    }
}