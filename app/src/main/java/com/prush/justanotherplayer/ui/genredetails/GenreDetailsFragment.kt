package com.prush.justanotherplayer.ui.genredetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.HeaderRecyclerFragment
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Genre
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsActivity
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsFragment
import com.prush.justanotherplayer.utils.PLAY_CONTEXT
import com.prush.justanotherplayer.utils.PLAY_CONTEXT_TYPE
import com.prush.justanotherplayer.utils.SELECTED_TRACK_POSITION
import com.prush.justanotherplayer.utils.SELECTED_GENRE_ID

import kotlinx.android.synthetic.main.header_recylerview_layout.*

class GenreDetailsFragment : HeaderRecyclerFragment(), GenreDetailsContract.View,
    RecyclerAdapter.OnItemInteractedListener, RecyclerAdapter.OnCarousalItemClickListener {

    private lateinit var adapter: RecyclerAdapter<Track>
    private lateinit var listPresenter: GenreDetailsListPresenter
    private lateinit var genreDetailsPresenter: GenreDetailsPresenter
    private var genreId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            genreId = requireArguments().getLong(GENRE_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        genreDetailsPresenter = GenreDetailsPresenter(Injection.provideGenreRepository(), this)
        listPresenter = GenreDetailsListPresenter(this)

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        genreDetailsPresenter.fetchGenreDetails(genreId)
    }

    override fun displayGenreDetails(genre: Genre) {

        headerAlbumArtImageView.setImageResource(R.drawable.ic_genre)

        if (genre.name.isNotEmpty() && context != null) {

            collapsingToolbarLayout.title = genre.name
            listPresenter.setItemsList(genre.tracksList, adapter)
            listPresenter.childItemsList = genre.albumsList
        }
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        genreDetailsPresenter.prepareTrackPlayback(selectedTrackPosition)
    }

    override fun onCarousalItemClick(selectedItemPosition: Int) {
        genreDetailsPresenter.loadAlbumDetails(listPresenter.childItemsList[selectedItemPosition])
    }

    override fun displayAlbumDetails(album: Album) {
        val intent = Intent(activity, AlbumDetailsActivity::class.java).apply {
            putExtra(AlbumDetailsFragment.ALBUM_ID, album.albumId)
        }
        startActivity(intent)
    }

    override fun startTrackPlayback(selectedTrackPosition: Int) {

        val intent = Intent(getViewActivity(), AudioPlayerService::class.java)
        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
        intent.putExtra(PLAY_CONTEXT_TYPE, PLAY_CONTEXT.GENRE_TRACKS)
        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
        intent.putExtra(SELECTED_GENRE_ID, genreId)
        Util.startForegroundService(getViewActivity(), intent)
    }

    override fun onContextMenuClicked(position: Int) {
        super.onContextMenuClicked(position)
        genreDetailsPresenter.prepareTrackContextMenu(position)
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    override fun shouldDisplayHomeAsUpEnabled(): Boolean {
        return true
    }

    companion object {
        const val GENRE_ID = "GenreId"

        fun newInstance(genreId: Long): GenreDetailsFragment {

            return GenreDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(GENRE_ID, genreId)
                }
            }
        }
    }
}