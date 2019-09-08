package com.prush.justanotherplayer.base

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.services.AudioPlayerService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.now_playing_bottom_sheet.*

private val TAG = BaseNowPlayingFooterActivity::class.java.name

@SuppressLint("Registered")
abstract class BaseNowPlayingFooterActivity : AppCompatActivity(), Player.EventListener {

    protected lateinit var audioPlayer: SimpleExoPlayer
    private var boundToService: Boolean = false
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

        onViewCreated(savedInstanceState)
    }

    open fun onViewCreated(savedInstanceState: Bundle?) {
        // client can override to take any action on view created
    }

    open fun getLayoutResourceId(): Int {
        return R.layout.base_now_playing_footer_layout
    }

    override fun onStart() {
        super.onStart()

        boundToService = bindService(
            Intent(this, AudioPlayerService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            if (service is AudioPlayerService.AudioServiceBinder) {
                audioPlayer = service.getPlayerInstance()

                playerControlView.player = audioPlayer
                shortPlayerControlView.player = audioPlayer

                audioPlayer.addListener(this@BaseNowPlayingFooterActivity)

                onConnectedToService()
            }
        }
    }

    open fun onConnectedToService() {
        // client can override to take any action on service bind
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

    protected fun toggleSheetBehavior() {

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onStop() {
        super.onStop()

        playerControlView.player = null
        shortPlayerControlView.player = null

        if (boundToService) {
            audioPlayer.removeListener(this)
            unbindService(serviceConnection)
        }
    }
}