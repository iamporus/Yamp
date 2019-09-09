package com.prush.justanotherplayer.ui.genreslibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Genre

class GenresListPresenter : ListPresenter<Genre> {

    override var rowLayoutId: Int = R.layout.genre_list_item_row

    override var itemsList: MutableList<Genre> = mutableListOf()

    override fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int)
            : BaseViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(rowLayoutId, parent, false)
        return GenreViewHolder(itemRowView)
    }

    override fun getItemsCount(): Int {
        return itemsList.size
    }

    override fun onBindTrackRowViewAtPosition(
        context: Context,
        rowView: ItemRowView,
        itemViewType: Int,
        position: Int,
        listener: RecyclerAdapter.OnItemClickListener
    ) {

        val genre = itemsList[position]
        rowView.setTitle(genre.name)
        rowView.setOnClickListener(position, listener)

        Glide.with(context)
            .asBitmap()
            .error(genre.defaultAlbumArtRes)
            .load(genre.defaultAlbumArtRes)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    rowView.setAlbumArt(resource)
                }

            })

    }

    override fun setItemsList(itemsList: MutableList<Genre>, adapter: RecyclerAdapter<Genre>) {
        this.itemsList.clear()
        this.itemsList.addAll(itemsList)
        adapter.notifyDataSetChanged()
    }

}