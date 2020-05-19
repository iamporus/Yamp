package com.prush.justanotherplayer.ui.artistdetails

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
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsActivity
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsFragment
import com.prush.justanotherplayer.utils.*
import kotlinx.android.synthetic.main.header_recylerview_layout.*

class ArtistDetailsFragment : HeaderRecyclerFragment(), ArtistDetailsContract.View,
    RecyclerAdapter.OnItemInteractedListener, RecyclerAdapter.OnCarousalItemClickListener {

    private lateinit var adapter: RecyclerAdapter<Track>
    private lateinit var listPresenter: ArtistDetailsListPresenter
    private lateinit var artistDetailsPresenter: ArtistDetailsPresenter
    private var artistId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            artistId = requireArguments().getLong(ARTIST_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistDetailsPresenter = ArtistDetailsPresenter(Injection.provideArtistRepository(), this)
        listPresenter = ArtistDetailsListPresenter(this)

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        artistDetailsPresenter.fetchArtistDetails(artistId)
    }

    override fun displayArtistDetails(artist: Artist) {

        headerAlbumArtImageView.setImageResource(R.drawable.ic_artist)

        if (artist.artistName.isNotEmpty() && context != null) {

            loadAlbumArt(
                getViewActivity(),
                artist.artistId,
                headerAlbumArtImageView,
                artist.defaultAlbumArtRes
            )

            collapsingToolbarLayout.title = artist.artistName
            listPresenter.setItemsList(artist.tracksList, adapter)
            listPresenter.childItemsList = artist.albumsList

        }
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        artistDetailsPresenter.prepareTrackPlayback(selectedTrackPosition)
    }

    override fun onCarousalItemClick(selectedItemPosition: Int) {
        artistDetailsPresenter.loadAlbumDetails(listPresenter.childItemsList[selectedItemPosition])
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
        intent.putExtra(PLAY_CONTEXT_TYPE, PLAY_CONTEXT.ARTIST_TRACKS)
        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
        intent.putExtra(SELECTED_ARTIST_ID, artistId)
        Util.startForegroundService(getViewActivity(), intent)
    }

    override fun onContextMenuClicked(position: Int) {
        super.onContextMenuClicked(position)
        artistDetailsPresenter.prepareTrackContextMenu(position)
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
        const val ARTIST_ID = "ArtistId"

        fun newInstance(artistId: Long): ArtistDetailsFragment {

            return ArtistDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARTIST_ID, artistId)
                }
            }
        }
    }
}