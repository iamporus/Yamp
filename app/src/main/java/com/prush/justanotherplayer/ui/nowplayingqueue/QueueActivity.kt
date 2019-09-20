package com.prush.justanotherplayer.ui.nowplayingqueue

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseServiceBoundedActivity
import com.prush.justanotherplayer.base.NowPlayingContract
import com.prush.justanotherplayer.base.NowPlayingPresenter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.queue.NowPlayingInfo
import com.prush.justanotherplayer.queue.NowPlayingQueue
import kotlinx.android.synthetic.main.base_container_layout.*

class QueueActivity : BaseServiceBoundedActivity(), NowPlayingContract.View, Player.EventListener {

    private lateinit var audioPlayer: SimpleExoPlayer
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

        audioPlayer = audioPlayerInstance

        audioPlayer.addListener(this)

        if (audioPlayerInstance.currentTag != null) {
            val nowPlayingInfo: NowPlayingInfo = audioPlayerInstance.currentTag as NowPlayingInfo
            nowPlayingPresenter.fetchTrackMetadata(nowPlayingInfo.id)
        }
        queueFragment.onConnectedToService(nowPlayingQueueInstance)
    }

    override fun displayError(error: String) {
        Snackbar.make(rootLayout, error, Snackbar.LENGTH_SHORT).show()
    }


    override fun showNowPlayingTrackMetadata(track: Track) {
        //nothing TODO here
    }

    override fun updateShuffleMode(shuffleMode: Boolean) {
        //nothing TODO here
    }

    override fun onPositionDiscontinuity(reason: Int) {
        queueFragment.updateNowPlaying()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.removeListener(this)
    }

    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    override fun getContext(): Context {
        return applicationContext
    }

}