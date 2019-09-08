package com.prush.justanotherplayer.artistslibrary

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Artist
import kotlinx.android.synthetic.main.base_recylerview_layout.*

class ArtistsLibraryFragment : BaseRecyclerFragment(), ArtistsContract.View,
    RecyclerAdapter.OnItemClickListener {

    private lateinit var artistPresenter: ArtistsContract.Presenter

    private lateinit var listPresenter: ListPresenter<Artist>

    private lateinit var adapter: RecyclerAdapter<Artist>

    override fun getBaseLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(activity, 2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listPresenter = ArtistsListPresenter()
        artistPresenter = ArtistPresenter(Injection.provideArtistRepository(), this)
        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        artistPresenter.loadLibraryArtists()
    }

    override fun displayAllArtists(artistList: MutableList<Artist>) {
        listPresenter.setItemsList(artistList, adapter)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        artistPresenter.onCleanup()
    }

    companion object {
        fun newInstance() = ArtistsLibraryFragment()
    }
}