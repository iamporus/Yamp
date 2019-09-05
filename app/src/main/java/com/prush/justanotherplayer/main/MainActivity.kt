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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.TrackRepository
import com.prush.justanotherplayer.services.AudioPlayerService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_player_bottom_sheet_controller.*
import kotlinx.android.synthetic.main.now_playing_bottom_sheet.*

class MainActivity : AppCompatActivity(), IMainActivityView,
    TracksRecyclerAdapter.OnItemClickListener, Player.EventListener {

    private val STORAGE_PERMISSION_ALREADY_ASKED = "storagePermissionAlreadyAsked"

    private var bAlreadyAskedForStoragePermission: Boolean = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var audioPlayer: SimpleExoPlayer
    private lateinit var presenter: MainActivityPresenter
    private lateinit var tracksListPresenter: TracksListPresenter
    private lateinit var adapter: TracksRecyclerAdapter

    companion object {
        private val TAG = MainActivity::class.java.name
        private const val READ_EXTERNAL_STORAGE_REQ_CODE: Int = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            bAlreadyAskedForStoragePermission =
                savedInstanceState.getBoolean(STORAGE_PERMISSION_ALREADY_ASKED, false)
        }

        nowPlayingBottomSheet.setOnClickListener { toggleSheetBehavior() }
        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        tracksListPresenter = TracksListPresenter()

        adapter = TracksRecyclerAdapter(tracksListPresenter, this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        val trackRepository = TrackRepository(applicationContext)

        presenter = MainActivityPresenter(this, trackRepository)

        if (!bAlreadyAskedForStoragePermission) {

            requestPermissionsWithRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE_REQ_CODE
            )
        }

        bindService(
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
                    presenter.onTrackPlaybackStarted(audioPlayer.currentTag as Long)
            }
        }

    }

    override fun displayError() {
        Log.d(TAG, "Oops. Something wrong with the sdcard.")
        Snackbar.make(rootLayout, R.string.error_sdcard, Snackbar.LENGTH_SHORT).show()
    }

    override fun displayEmptyLibrary() {

        Log.d(TAG, "Oops. You don't have any tracks.")
        //TODO: display empty view
    }

    override fun displayLibraryTracks(trackList: MutableList<Track>) {

        Log.d(TAG, "Loaded some tracks")
        tracksListPresenter.setTrackList(trackList)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    private fun toggleSheetBehavior() {

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun requestPermissionsWithRationale(permission: String, requestCode: Int) {

        Log.d(TAG, "Requesting permission for accessing storage.")

        if (ContextCompat.checkSelfPermission(
                applicationContext, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            bAlreadyAskedForStoragePermission = true
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionRationale(permission)
            } else {
                requestPermission(permission, requestCode)
            }
        } else {
            //permission is granted
            presenter.displayAllTracks()
        }
    }

    private fun requestPermission(permission: String, requestCode: Int) {

        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
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
                    presenter.onPermissionGranted()
                } else {
                    presenter.onPermissionDenied(permissions[0])
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
                requestPermission(permission, READ_EXTERNAL_STORAGE_REQ_CODE)
            }
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(STORAGE_PERMISSION_ALREADY_ASKED, bAlreadyAskedForStoragePermission)
        super.onSaveInstanceState(outState)
    }

    override fun onItemClick(tracksList: MutableList<Track>, selectedTrackPosition: Int) {
        presenter.onTrackSelected(tracksList, selectedTrackPosition)
    }

    override fun updatePlaybackMetadata(track: Track) {

        Log.d(TAG, "updatePlaybackMetadata with ${track.title}")
        if (track.albumArtBitmap != null) {
            shortAlbumArtImageView.setImageBitmap(track.albumArtBitmap)
            albumArtImageView.setImageBitmap(track.albumArtBitmap)
        } else {
            Glide.with(this)
                .load(track.getAlbumArtUri(this))
                .into(shortAlbumArtImageView)
            Glide.with(this)
                .load(track.getAlbumArtUri(this))
                .into(albumArtImageView)
        }

        titleTextView.text = track.title
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(TAG, "onPlayerStateChanged with $playbackState")
        when (playbackState) {
            PlaybackState.STATE_PLAYING,
            PlaybackState.STATE_PAUSED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                if (audioPlayer.currentTag != null)
                    presenter.onTrackPlaybackStarted(audioPlayer.currentTag as Long)

            }
            else -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        presenter.onCleanup()
    }
}