package com.prush.justanotherplayer.ui.albumdetails

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
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.utils.SELECTED_TRACK_POSITION
import com.prush.justanotherplayer.utils.TRACKS_LIST
import com.prush.justanotherplayer.utils.loadAlbumArt
import kotlinx.android.synthetic.main.header_recylerview_layout.*

class AlbumDetailsFragment : HeaderRecyclerFragment(), AlbumDetailsContract.View,
    RecyclerAdapter.OnItemInteractedListener {

    private lateinit var adapter: RecyclerAdapter<Track>
    private lateinit var listPresenter: AlbumTracksListPresenter
    private lateinit var albumDetailsPresenter: AlbumDetailsPresenter
    private var albumId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            albumId = requireArguments().getLong(ALBUM_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        albumDetailsPresenter = AlbumDetailsPresenter(Injection.provideAlbumRepository(), this)
        listPresenter = AlbumTracksListPresenter()

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        albumDetailsPresenter.fetchAlbumDetails(albumId)
    }

    override fun displayAlbumDetails(album: Album) {

        headerAlbumArtImageView.setImageResource(R.drawable.ic_album)

        if (album.albumName.isNotEmpty() && context != null) {

            loadAlbumArt(
                getViewActivity(),
                album.albumId,
                headerAlbumArtImageView,
                album.defaultAlbumArtRes
            )

            collapsingToolbarLayout.title = album.albumName
            listPresenter.setItemsList(album.tracksList, adapter)

        }
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        albumDetailsPresenter.prepareTrackPlayback(selectedTrackPosition)
    }

    override fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>) {

        val intent = Intent(getViewActivity(), AudioPlayerService::class.java)
        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
        intent.putExtra(TRACKS_LIST, ArrayList(tracksList))
        Util.startForegroundService(getViewActivity(), intent)
    }

    override fun onContextMenuClicked(position: Int) {
        super.onContextMenuClicked(position)
        albumDetailsPresenter.prepareTrackContextMenu(position)
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
        const val ALBUM_ID = "AlbumId"

        fun newInstance(albumId: Long): AlbumDetailsFragment {

            return AlbumDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ALBUM_ID, albumId)
                }
            }
        }
    }
}