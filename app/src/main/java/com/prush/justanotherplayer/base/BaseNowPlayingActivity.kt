package com.prush.justanotherplayer.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.session.PlaybackState
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.NowPlayingInfo
import com.prush.justanotherplayer.services.NowPlayingQueue
import com.prush.justanotherplayer.ui.nowplayingqueue.QueueActivity
import com.prush.justanotherplayer.utils.getAlbumArtUri
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_player_bottom_sheet_controller.*
import kotlinx.android.synthetic.main.exo_player_bottom_sheet_controller_large.*
import kotlinx.android.synthetic.main.now_playing_bottom_sheet.*

private val TAG = BaseNowPlayingActivity::class.java.name

@SuppressLint("Registered")
abstract class BaseNowPlayingActivity : BaseServiceBoundedActivity(), NowPlayingContract.View,
    Player.EventListener {

    private lateinit var audioPlayer: SimpleExoPlayer
    private lateinit var nowPlayingPresenter: NowPlayingPresenter

    @Suppress("MemberVisibilityCanBePrivate")
    protected lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resourceId = getLayoutResourceId()
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(resourceId, null)

        if (view.findViewById<View>(R.id.nowPlayingBottomSheet) == null) {
            throw IllegalAccessException("Child layout must have have a BottomSheet with id R.id.nowPlayingBottomSheet")
        }

        setContentView(resourceId)

        setSupportActionBar(toolbar)

        setBottomSheet()

        nowPlayingPresenter = NowPlayingPresenter(
            this,
            Injection.provideTrackRepository()
        )

        onViewCreated(savedInstanceState)
    }

    open fun onViewCreated(savedInstanceState: Bundle?) {
        // client can override to take any action on view created
    }

    open fun getLayoutResourceId(): Int {
        return R.layout.base_now_playing_footer_layout
    }

    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    override fun getContext(): Context {
        return applicationContext
    }

    override fun onConnectedToService(
        audioPlayerInstance: SimpleExoPlayer,
        nowPlayingQueueInstance: NowPlayingQueue
    ) {

        audioPlayer = audioPlayerInstance

        playerControlView.player = audioPlayer
        shortPlayerControlView.player = audioPlayer

        audioPlayer.addListener(this@BaseNowPlayingActivity)

        if (audioPlayer.currentTag != null) {
            val nowPlayingInfo: NowPlayingInfo = audioPlayer.currentTag as NowPlayingInfo
            nowPlayingPresenter.fetchTrackMetadata(nowPlayingInfo.id)
        }
    }

    private fun setBottomSheet() {

        nowPlayingQueueButton.setOnClickListener { navigateToNowPlayingQueue() }
        bottomSheetToolbar.setOnClickListener { toggleSheetBehavior() }

        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED -> {
                        albumArtImageView.alpha = 0f
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        setSupportActionBar(bottomSheetToolbar)
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        bottomSheetToolbar.title = ""
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        shortPlayerControlView.alpha = 0f
                        albumArtImageView.alpha = 1f
                        bottomSheetToolbar.alpha = 1f
                    }
                    else -> {
                        //do nothing
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                shortPlayerControlView.alpha = (1 - slideOffset)
                albumArtImageView.alpha = slideOffset
                bottomSheetToolbar.alpha = slideOffset
            }

        })
    }

    private fun navigateToNowPlayingQueue() {

        val intent = Intent(this, QueueActivity::class.java)
        startActivity(intent)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun toggleSheetBehavior() {

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
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
                .asBitmap()
                .load(getAlbumArtUri(this, track.albumId))
                .placeholder(R.drawable.playback_track_icon)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        albumArtImageView.setImageBitmap(resource)
                        Palette.from(resource).generate { palette ->
                            val textSwatch = palette?.darkMutedSwatch
                            if (textSwatch == null)
                                Log.d(TAG, "Swatch is null.")
                            else {

                                val gradientDrawable = GradientDrawable(
                                    GradientDrawable.Orientation.TOP_BOTTOM,
                                    intArrayOf(textSwatch.rgb, R.color.colorPrimary)
                                )

                                gradientView.background = gradientDrawable
                            }
                        }
                    }

                })
        }

        titleTextView.text = track.title
        nowPlayingTitleTextView.text = track.title
        nowPlayingSubtitleTextView.text = track.artistName

        shuffleBtn.setOnClickListener {
            it.isActivated = !it.isActivated
        }

        // pop up bottom sheet
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            albumArtImageView.alpha = 0f
        }

        nowPlayingQueueButton.visibility = View.VISIBLE


    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(TAG, "onPlayerStateChanged with $playbackState")
        when (playbackState) {
            PlaybackState.STATE_PLAYING,
            PlaybackState.STATE_PAUSED -> {

                if (audioPlayer.currentTag != null) {
                    val nowPlayingInfo: NowPlayingInfo = audioPlayer.currentTag as NowPlayingInfo
                    nowPlayingPresenter.fetchTrackMetadata(nowPlayingInfo.id)
                }

            }
            PlaybackState.STATE_NONE, PlaybackState.STATE_STOPPED -> {
                nowPlayingQueueButton.visibility = View.GONE
            }
            else -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        when (repeatMode) {
            Player.REPEAT_MODE_ONE -> {
                exo_repeat_toggle.setImageResource(R.drawable.ic_repeat_one)
            }
            Player.REPEAT_MODE_ALL ->{
                exo_repeat_toggle.setImageResource(R.drawable.ic_repeat)
            }
            Player.REPEAT_MODE_OFF -> {
                exo_repeat_toggle.setImageResource(R.drawable.ic_repeat_off)
            }
        }
    }

    // gets called when current track playback completes and playback of next track starts
    override fun onPositionDiscontinuity(reason: Int) {
        if (audioPlayer.currentTag != null) {
            val nowPlayingInfo: NowPlayingInfo = audioPlayer.currentTag as NowPlayingInfo
            nowPlayingPresenter.fetchTrackMetadata(nowPlayingInfo.id)
        }
    }

    override fun displayError(error: String) {
        Snackbar.make(rootLayout, error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            toggleSheetBehavior()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onStop() {
        super.onStop()

        if (boundToService) {

            playerControlView.player = null
            shortPlayerControlView.player = null

            audioPlayer.removeListener(this)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        nowPlayingPresenter.onCleanup()
    }
}