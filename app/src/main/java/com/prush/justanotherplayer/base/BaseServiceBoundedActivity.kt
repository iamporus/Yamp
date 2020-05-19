package com.prush.justanotherplayer.base

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.queue.NowPlayingQueue

private val TAG = AudioPlayerService::class.java.name

@SuppressLint("Registered")
open class BaseServiceBoundedActivity : AppCompatActivity() {

    protected var boundToService: Boolean = false

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
                onConnectedToService(service.getPlayerInstance(), service.getNowPlayingQueue())
            }
        }
    }

    open fun onConnectedToService(
        audioPlayerInstance: SimpleExoPlayer,
        nowPlayingQueueInstance: NowPlayingQueue
    ) {
        // client can override to take any action on service bind
    }

    override fun onStop() {
        super.onStop()
        if (boundToService) {
            unbindService(serviceConnection)
        }
    }
}