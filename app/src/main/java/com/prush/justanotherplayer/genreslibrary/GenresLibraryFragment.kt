package com.prush.justanotherplayer.genreslibrary

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
import com.prush.justanotherplayer.model.Genre
import kotlinx.android.synthetic.main.base_recylerview_layout.*

class GenresLibraryFragment : BaseRecyclerFragment(), GenresContract.View,
    RecyclerAdapter.OnItemClickListener {

    private lateinit var genresPresenter: GenresContract.Presenter

    private lateinit var listPresenter: ListPresenter<Genre>

    private lateinit var adapter: RecyclerAdapter<Genre>

    override fun getBaseLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(activity, 2)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listPresenter = GenresListPresenter()
        genresPresenter = GenrePresenter(Injection.provideGenreRepository(), this)
        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        genresPresenter.loadLibraryGenres()
    }

    override fun displayAllGenres(genresList: MutableList<Genre>) {
        listPresenter.setItemsList(genresList, adapter)
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

    override fun onDestroy() {
        super.onDestroy()
        genresPresenter.onCleanup()
    }

    companion object {
        fun newInstance() = GenresLibraryFragment()
    }
}