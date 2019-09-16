package com.prush.justanotherplayer.ui.artistdetails

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.base.HeaderRecyclerFragment
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Artist
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.services.SELECTED_TRACK_POSITION
import com.prush.justanotherplayer.services.TRACKS_LIST
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsActivity
import com.prush.justanotherplayer.ui.albumdetails.AlbumDetailsFragment
import com.prush.justanotherplayer.utils.getAlbumArtUri
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
            artistId = arguments!!.getLong(ARTIST_ID)
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

        if (artist.artistName.isNotEmpty() && context != null) {

            Glide.with(getViewActivity())
                .asBitmap()
                .error(artist.defaultAlbumArtRes)
                .load(getAlbumArtUri(getViewActivity(), artist.artistId))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        headerAlbumArtImageView.setImageBitmap(resource)
                    }

                })

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

    override fun startTrackPlayback(selectedTrackPosition: Int, tracksList: MutableList<Track>) {

        val intent = Intent(getViewActivity(), AudioPlayerService::class.java)
        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
        intent.putExtra(SELECTED_TRACK_POSITION, selectedTrackPosition)
        intent.putExtra(TRACKS_LIST, ArrayList(tracksList))
        Util.startForegroundService(getViewActivity(), intent)
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