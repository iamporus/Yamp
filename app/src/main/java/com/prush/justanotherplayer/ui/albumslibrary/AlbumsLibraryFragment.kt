package com.prush.justanotherplayer.ui.albumslibrary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsActivity
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsFragment.Companion.ALBUM_ID
import kotlinx.android.synthetic.main.base_recylerview_layout.*

class AlbumsLibraryFragment : BaseRecyclerFragment(), AlbumsContract.View,
    RecyclerAdapter.OnItemInteractedListener {

    private lateinit var albumsPresenter: AlbumsContract.Presenter

    private lateinit var listPresenter: ListPresenter<Album>

    private lateinit var adapter: RecyclerAdapter<Album>

    override fun getBaseLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(activity, 2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listPresenter = AlbumsListPresenter()
        albumsPresenter = AlbumPresenter(Injection.provideAlbumRepository(), this)
        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        albumsPresenter.loadLibraryAlbums()
    }

    override fun displayAllAlbums(albumsList: MutableList<Album>) {
        listPresenter.setItemsList(albumsList, adapter)
    }

    override fun showAlbumDetails(album: Album) {
        val intent = Intent(activity, AlbumDetailsActivity::class.java).apply {
            putExtra(ALBUM_ID, album.albumId)
        }
        startActivity(intent)
    }

    override fun displayEmptyLibrary() {
        showEmptyLibrary()
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        albumsPresenter.loadAlbumDetails(selectedTrackPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
        albumsPresenter.onCleanup()
    }

    companion object {
        fun newInstance() = AlbumsLibraryFragment()
    }
}