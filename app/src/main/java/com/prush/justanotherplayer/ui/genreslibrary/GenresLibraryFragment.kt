package com.prush.justanotherplayer.ui.genreslibrary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Genre
import com.prush.justanotherplayer.ui.genredetails.GenreDetailsActivity
import com.prush.justanotherplayer.ui.genredetails.GenreDetailsFragment
import kotlinx.android.synthetic.main.base_recylerview_layout.*

class GenresLibraryFragment : BaseRecyclerFragment(), GenresContract.View,
    RecyclerAdapter.OnItemClickListener {

    private lateinit var genresPresenter: GenresContract.Presenter

    private lateinit var listPresenter: ListPresenter<Genre>

    private lateinit var adapter: RecyclerAdapter<Genre>

    override fun getBaseLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
        showEmptyLibrary()
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        genresPresenter.loadGenreDetails(selectedTrackPosition)
    }

    override fun displayGenreDetails(genre: Genre) {
        val intent = Intent(activity, GenreDetailsActivity::class.java).apply {
            putExtra(GenreDetailsFragment.GENRE_ID, genre.id)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        genresPresenter.onCleanup()
    }

    companion object {
        fun newInstance() = GenresLibraryFragment()
    }
}