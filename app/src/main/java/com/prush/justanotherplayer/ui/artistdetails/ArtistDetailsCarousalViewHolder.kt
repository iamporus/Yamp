package com.prush.justanotherplayer.ui.artistdetails

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.ItemRowView
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.ui.albumslibrary.AlbumsListPresenter

class ArtistDetailsCarousalViewHolder(itemView: View) : BaseViewHolder(itemView),
    ArtistDetailsCarousalRowView {

    private lateinit var listPresenter: AlbumCarousalListPresenter
    private var recyclerView: RecyclerView = itemView.findViewById(R.id.carousalRecyclerView)
    lateinit var adapter: RecyclerAdapter<Album>

    override fun setupLayoutManager() {

        recyclerView.apply {

            layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            isNestedScrollingEnabled = false
        }

    }

    override fun setupAdapter(clickListener: RecyclerAdapter.OnItemClickListener) {

        listPresenter = AlbumCarousalListPresenter()
        adapter = RecyclerAdapter(listPresenter, clickListener)
        recyclerView.adapter = adapter
    }

    override fun setupData(childItemsList: MutableList<Album>) {
        listPresenter.setItemsList(childItemsList, adapter)
    }

}

interface ArtistDetailsCarousalRowView : ItemRowView {

    fun setupLayoutManager()

    fun setupAdapter(clickListener: RecyclerAdapter.OnItemClickListener)

    fun setupData(childItemsList: MutableList<Album>)
}

class AlbumCarousalListPresenter : AlbumsListPresenter() {

    override var rowLayoutId: Int = R.layout.carousal_album_list_item_row
}
