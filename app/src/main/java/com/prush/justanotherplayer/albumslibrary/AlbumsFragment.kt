package com.prush.justanotherplayer.albumslibrary

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Album
import kotlinx.android.synthetic.main.base_recylerview_layout.*

class AlbumsFragment : BaseRecyclerFragment(), AlbumsContract.View,
    RecyclerAdapter.OnItemClickListener {

    override lateinit var albumsPresenter: AlbumsContract.Presenter

    private lateinit var listPresenter: ListPresenter<Album>

    private lateinit var adapter: RecyclerAdapter<Album>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listPresenter = AlbumsListPresenter()
        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun displayAllAlbums(albumsList: MutableList<Album>) {
        listPresenter.setItemsList(albumsList, adapter)
    }

    override fun displayEmptyLibrary() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance() = AlbumsFragment()
    }
}