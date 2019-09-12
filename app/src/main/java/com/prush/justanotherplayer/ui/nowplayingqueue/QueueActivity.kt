package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseServiceBoundedActivity
import com.prush.justanotherplayer.base.NowPlayingContract
import com.prush.justanotherplayer.base.NowPlayingPresenter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.NowPlayingQueue
import com.prush.justanotherplayer.utils.getAlbumArtUri
import kotlinx.android.synthetic.main.base_container_layout.*

class QueueActivity : BaseServiceBoundedActivity(), NowPlayingContract.View {

    private lateinit var queueFragment: QueueFragment
    private lateinit var nowPlayingPresenter: NowPlayingPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.base_container_layout)

        queueFragment = supportFragmentManager.findFragmentById(R.id.container)
                as QueueFragment? ?: QueueFragment.newInstance()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, queueFragment)
            commit()
        }


        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nowPlayingPresenter = NowPlayingPresenter(
            this,
            Injection.provideTrackRepository()
        )

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onConnectedToService(
        audioPlayerInstance: SimpleExoPlayer,
        nowPlayingQueueInstance: NowPlayingQueue
    ) {
        super.onConnectedToService(audioPlayerInstance, nowPlayingQueueInstance)

        nowPlayingPresenter.fetchTrackMetadata(audioPlayerInstance.currentTag as Long)
        queueFragment.onConnectedToService(nowPlayingQueueInstance)
    }

    override fun displayError(error: String) {
        Snackbar.make(rootLayout, error, Snackbar.LENGTH_SHORT).show()
    }


    override fun showNowPlayingTrackMetadata(track: Track) {
        headerTitleTextView.text = track.title
        headerSubTitleTextView.text = getString(R.string.by_artist, track.artistName)

        Glide.with(this)
            .load(getAlbumArtUri(this, track.albumId))
            .error(R.drawable.playback_track_icon)
            .into(headerAlbumArtImageView)

    }

    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    override fun getContext(): Context {
        return applicationContext
    }

}