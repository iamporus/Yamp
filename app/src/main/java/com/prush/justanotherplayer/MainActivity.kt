package com.prush.justanotherplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.repositories.TrackRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IMainActivityView {

    private lateinit var presenter: MainActivityPresenter
    private val TAG = javaClass.name
    private val READ_EXTERNAL_STORAGE_REQ_CODE: Int = 101

    override fun displayError() {
        Log.d(TAG, "Oops. Something wrong with the sdcard.")
    }

    override fun displayEmptyLibrary() {

        Log.d(TAG, "Oops. You don't have any tracks.")
    }

    override fun displayLibraryTracks(trackList: MutableList<Track>) {

        Log.d(TAG, "Loaded some tracks")
    }

    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val trackRepository = TrackRepository(applicationContext)
        presenter = MainActivityPresenter(this, trackRepository)
        if (Build.VERSION.SDK_INT > 15)
            presenter.requestPermissionsWithRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE_REQ_CODE
            )
        else
            presenter.displayAllTracks()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onPermissionGranted()
                } else {
                    presenter.onPermissionDenied(permissions[0])
                }
            }
        }
    }

    override fun showPermissionRationale(permission: String) {
        Snackbar.make(rootLayout, R.string.permissions_rationale, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.okay) {
                presenter.requestPermission(permission, READ_EXTERNAL_STORAGE_REQ_CODE)
            }
            .show()
    }


}
