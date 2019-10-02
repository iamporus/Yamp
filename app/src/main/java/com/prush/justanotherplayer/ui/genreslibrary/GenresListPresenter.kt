package com.prush.justanotherplayer.ui.genreslibrary

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Genre
import com.prush.justanotherplayer.utils.OnBitmapLoadedListener
import com.prush.justanotherplayer.utils.loadAlbumArt

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
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {

        val genre = itemsList[position]
        rowView.setTitle(genre.name)
        rowView.setOnClickListener(position, listener)

        loadAlbumArt(context, genre.defaultAlbumArtRes, object : OnBitmapLoadedListener {
            override fun onBitmapLoaded(resource: Bitmap) {
                rowView.setAlbumArt(resource)
            }

            override fun onBitmapLoadingFailed() {
                rowView.setAlbumArt(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_genre
                    )!!.toBitmap()
                )
            }

        })

    }

    override fun setItemsList(itemsList: MutableList<Genre>, adapter: RecyclerAdapter<Genre>) {
        this.itemsList.clear()
        this.itemsList.addAll(itemsList)
        adapter.notifyDataSetChanged()
    }

}