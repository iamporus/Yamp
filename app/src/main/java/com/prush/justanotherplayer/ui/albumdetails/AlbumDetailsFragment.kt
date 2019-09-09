package com.prush.justanotherplayer.ui.albumdetails

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prush.justanotherplayer.base.HeaderRecyclerFragment
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Album
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.getAlbumArtUri
import kotlinx.android.synthetic.main.header_recylerview_layout.*

class AlbumDetailsFragment : HeaderRecyclerFragment(), AlbumDetailsContract.View,
    RecyclerAdapter.OnItemClickListener {
    override fun onItemClick(selectedTrackPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var adapter: RecyclerAdapter<Track>
    private lateinit var listPresenter: AlbumTracksListPresenter
    private lateinit var albumDetailsPresenter: AlbumDetailsPresenter
    private var albumId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            albumId = arguments!!.getLong(ALBUM_ID)
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

        if (album.albumName.isNotEmpty() && context != null) {

            Glide.with(getViewActivity())
                .asBitmap()
                .error(album.defaultAlbumArtRes)
                .load(getAlbumArtUri(getViewActivity(), album.albumId))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        headerAlbumArtImageView.setImageBitmap(resource)
                    }

                })

            collapsingToolbarLayout.title = album.albumName
            listPresenter.setItemsList(album.tracksList, adapter)

        }
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