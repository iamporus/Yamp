package com.prush.justanotherplayer.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.trackslibrary.TracksLibraryFragment
import com.prush.justanotherplayer.trackslibrary.TracksPresenter
import com.prush.justanotherplayer.utils.PermissionCallbacks
import com.prush.justanotherplayer.utils.PermissionUtils
import com.prush.justanotherplayer.utils.getAlbumArtUri
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_player_bottom_sheet_controller.*
import kotlinx.android.synthetic.main.now_playing_bottom_sheet.*

private const val KEY_STORAGE_PERMISSION_ALREADY_ASKED = "storagePermissionAlreadyAsked"
private val TAG = MainActivity::class.java.name
private const val READ_EXTERNAL_STORAGE_REQ_CODE: Int = 101

class MainActivity : AppCompatActivity(), IMainActivityView, Player.EventListener,
    PermissionCallbacks {

    private lateinit var tracksPresenter: TracksPresenter
    private var bAlreadyAskedForStoragePermission: Boolean = false
    private var boundToService: Boolean = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var audioPlayer: SimpleExoPlayer
    private lateinit var presenter: MainActivityPresenter
    private lateinit var permissionUtils: PermissionUtils


    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    override fun getContext(): Context {
        return applicationContext
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            bAlreadyAskedForStoragePermission =
                savedInstanceState.getBoolean(KEY_STORAGE_PERMISSION_ALREADY_ASKED, false)
        }

        val tracksLibraryFragment = supportFragmentManager.findFragmentById(R.id.container)
                as TracksLibraryFragment? ?: TracksLibraryFragment.newInstance()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, tracksLibraryFragment)
            commit()
        }

        presenter = MainActivityPresenter(
            this,
            Injection.provideTrackRepository()
        )

        tracksPresenter = TracksPresenter(
            Injection.provideTrackRepository(),
            tracksLibraryFragment,
            this
        )

        setBottomSheet()


        permissionUtils = PermissionUtils()

        if (!bAlreadyAskedForStoragePermission) {

            bAlreadyAskedForStoragePermission = permissionUtils.requestPermissionsWithRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE_REQ_CODE,
                this
            )
        }
    }

    private fun setBottomSheet() {

        nowPlayingBottomSheet.setOnClickListener { toggleSheetBehavior() }

        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED -> {
                        albumArtImageView.alpha = 0f
                    }
                    else -> {
                        //do nothing
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                shortPlayerControlView.alpha = (1 - slideOffset)
                albumArtImageView.alpha = slideOffset
            }

        })
    }

    override fun onStart() {
        super.onStart()

        boundToService = bindService(
            Intent(this, AudioPlayerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            if (service is AudioPlayerService.AudioServiceBinder) {
                audioPlayer = service.getPlayerInstance()

                playerControlView.player = audioPlayer
                shortPlayerControlView.player = audioPlayer

                audioPlayer.addListener(this@MainActivity)

                if (audioPlayer.currentTag != null)
                    presenter.fetchTrackMetadata(audioPlayer.currentTag as Long)
            }
        }

    }

    override fun displayError() {
        Log.d(TAG, "Oops. Something wrong with the sdcard.")
        Snackbar.make(rootLayout, R.string.error_sdcard, Snackbar.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        progressBar.show()
    }

    override fun hideProgress() {
        progressBar.hide()
    }

    private fun toggleSheetBehavior() {

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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

        tracksPresenter.setNowPlayingTrack(track.id)
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

    override fun onShowPermissionRationale(permission: String) {
        showPermissionRationale(permission)
    }

    override fun onPermissionGranted(permission: String) {
        tracksPresenter.loadLibraryTracks()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQ_CODE -> {

                bAlreadyAskedForStoragePermission = false

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tracksPresenter.loadLibraryTracks()
                } else {
                    showPermissionRationale(permissions[0])
                }
            }
        }
    }

    override fun showPermissionRationale(permission: String) {
        Snackbar.make(
            rootLayout,
            R.string.permissions_rationale, Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.okay) {
                permissionUtils.requestPermission(this, permission, READ_EXTERNAL_STORAGE_REQ_CODE)
            }
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(
            KEY_STORAGE_PERMISSION_ALREADY_ASKED,
            bAlreadyAskedForStoragePermission
        )
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()

        playerControlView.player = null
        shortPlayerControlView.player = null

        if (boundToService) {

            audioPlayer.removeListener(this)
            unbindService(connection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onCleanup()
    }
}
