package com.prush.justanotherplayer.main

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.util.Util
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.ITrackRepository
import com.prush.justanotherplayer.services.AudioPlayerService
import com.prush.justanotherplayer.services.TRACK_ID
import com.prush.justanotherplayer.services.TRACK_TITLE

class MainActivityPresenter(
    private val mainActivityView: IMainActivityView,
    private val trackRepository: ITrackRepository
) {
    private val TAG = javaClass.name

    fun displayAllTracks() {

        try {
            val trackList = trackRepository.getAllTracks()
            if (trackList.isEmpty())
                mainActivityView.displayEmptyLibrary()
            else
                mainActivityView.displayLibraryTracks(trackList)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Log.d(TAG, "Exception: ${e.message}")
            mainActivityView.displayError()
        }
    }

    fun requestPermissionsWithRationale(permission: String, requestCode: Int) {

        if (ContextCompat.checkSelfPermission(
                mainActivityView.getViewActivity().applicationContext,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mainActivityView.getViewActivity(),
                    permission
                )
            ) {
                mainActivityView.showPermissionRationale(permission)
            } else {
                requestPermission(permission, requestCode)
            }
        } else {
            //permission is granted
            displayAllTracks()
        }
    }

    fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
            mainActivityView.getViewActivity(),
            arrayOf(permission),
            requestCode
        )
    }

    fun onPermissionGranted() {
        displayAllTracks()
    }

    fun onPermissionDenied(permission: String) {
        mainActivityView.showPermissionRationale(permission)
    }

    fun onTrackSelected(track: Track) {

        Log.d(TAG, "Track selected for playback $track")

        val intent = Intent(mainActivityView.getViewActivity(), AudioPlayerService::class.java)
        intent.action = AudioPlayerService.PlaybackControls.PLAY.name
        intent.putExtra(TRACK_ID, track.id)
        intent.putExtra(TRACK_TITLE, track.title)
        Util.startForegroundService(mainActivityView.getViewActivity(), intent)
    }
}
