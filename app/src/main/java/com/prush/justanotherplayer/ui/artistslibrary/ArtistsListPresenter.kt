package com.prush.justanotherplayer.ui.artistslibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.base.ItemRowView

class ArtistsListPresenter : ListPresenter<Artist> {

    override var rowLayoutId: Int = R.layout.album_list_item_row

    override var itemsList: MutableList<Artist> = mutableListOf()

    override fun getItemsCount(): Int {
        return itemsList.size
    }

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {

        val artist = itemsList[position]
        rowView.setTitle(artist.artistName)
        rowView.setOnClickListener(position, listener)

        Glide.with(context)
            .asBitmap()
            .error(artist.defaultAlbumArtRes)
            .load(artist.defaultAlbumArtRes)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    rowView.setAlbumArt(resource)
                }

            })

    }

    override fun setItemsList(itemsList: MutableList<Artist>, adapter: RecyclerAdapter<Artist>) {
        if (this.itemsList.isNotEmpty()) {

            val trackDiffCallback = ArtistDiffCallback(this.itemsList, itemsList)
            val diffResult = DiffUtil.calculateDiff(trackDiffCallback)

            this.itemsList = itemsList
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