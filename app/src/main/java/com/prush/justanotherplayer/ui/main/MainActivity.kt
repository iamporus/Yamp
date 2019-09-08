package com.prush.justanotherplayer.ui.main

import android.content.Context
import android.media.session.PlaybackState
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseNowPlayingFooterActivity
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.PermissionUtils
import com.prush.justanotherplayer.utils.getAlbumArtUri
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_player_bottom_sheet_controller.*
import kotlinx.android.synthetic.main.now_playing_bottom_sheet.*

private val TAG = MainActivity::class.java.name

class MainActivity : BaseNowPlayingFooterActivity(), MainContract.View, Player.EventListener {

    private lateinit var presenter: MainActivityPresenter

    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    override fun getContext(): Context {
        return applicationContext
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        super.onViewCreated(savedInstanceState)

        setupViewPager()

        presenter = MainActivityPresenter(
            this,
            Injection.provideTrackRepository()
        )
    }

    private fun setupViewPager() {

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 3

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onConnectedToService() {

        if (audioPlayer.currentTag != null)
            presenter.fetchTrackMetadata(audioPlayer.currentTag as Long)
    }

    override fun displayError() {
        Log.d(TAG, "Oops. Something wrong with the sdcard.")
        Snackbar.make(rootLayout, R.string.error_sdcard, Snackbar.LENGTH_SHORT).show()
    }

    override fun showNowPlayingTrackMetadata(track: Track) {

        Log.d(TAG, "showNowPlayingTrackMetadata with ${track.title}")
        if (track.albumArtBitmap != null) {
            shortAlbumArtImageView.setImageBitmap(track.albumArtBitmap)
            albumArtImageView.setImageBitmap(track.albumArtBitmap)
        } else {
            Glide.with(this)
                .load(getAlbumArtUri(this, track.albumId))
                .into(shortAlbumArtImageView)
            Glide.with(this)
                .load(getAlbumArtUri(this, track.albumId))
                .placeholder(R.drawable.playback_track_icon)
                .into(albumArtImageView)
        }

        titleTextView.text = track.title
        nowPlayingTitleTextView.text = track.title
        nowPlayingSubtitleTextView.text =
            getString(R.string.now_playing_track_subtitle, track.albumName, track.artistName)

        // pop up bottom sheet
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            albumArtImageView.alpha = 0f
        }

        //TODO: figure out how to propagate this to tracksPresenter
//        tracksPresenter.setNowPlayingTrack(track.id)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(TAG, "onPlayerStateChanged with $playbackState")
        when (playbackState) {
            PlaybackState.STATE_PLAYING,
            PlaybackState.STATE_PAUSED -> {

                if (audioPlayer.currentTag != null)
                    presenter.fetchTrackMetadata(audioPlayer.currentTag as Long)

            }
            else -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    // gets called when current track playback completes and playback of next track starts
    override fun onPositionDiscontinuity(reason: Int) {
        if (audioPlayer.currentTag != null)
            presenter.fetchTrackMetadata(audioPlayer.currentTag as Long)
    }

    override fun showPermissionRationale(permission: String, requestCode: Int) {
        Snackbar.make(
            rootLayout,
            R.string.permissions_rationale, Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.okay) {
                PermissionUtils().requestPermission(this, permission, requestCode)
            }
            .show()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            toggleSheetBehavior()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleanup()
    }
}
