package com.prush.justanotherplayer.trackslibrary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.base.BaseRecyclerFragment
import com.prush.justanotherplayer.base.ListPresenter
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.di.Injection
import com.prush.justanotherplayer.main.MainActivity
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.PermissionCallbacks
import com.prush.justanotherplayer.utils.PermissionUtils
import kotlinx.android.synthetic.main.base_recylerview_layout.*

private val TAG = TracksLibraryFragment::class.java.name
private const val KEY_STORAGE_PERMISSION_ALREADY_ASKED = "storagePermissionAlreadyAsked"
const val READ_EXTERNAL_STORAGE_REQ_CODE: Int = 101

class TracksLibraryFragment : BaseRecyclerFragment(), TracksContract.View, PermissionCallbacks,
    RecyclerAdapter.OnItemClickListener {

    private lateinit var tracksPresenter: TracksContract.Presenter
    private lateinit var listPresenter: ListPresenter<Track>
    private lateinit var permissionUtils: PermissionUtils
    private var bAlreadyAskedForStoragePermission: Boolean = false

    private lateinit var adapter: RecyclerAdapter<Track>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionUtils = PermissionUtils()

        if (savedInstanceState != null) {
            bAlreadyAskedForStoragePermission =
                savedInstanceState.getBoolean(KEY_STORAGE_PERMISSION_ALREADY_ASKED, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: Inject these guys
        tracksPresenter = TracksPresenter(Injection.provideTrackRepository(), this)
        listPresenter = TracksListPresenter()

        adapter = RecyclerAdapter(listPresenter, this)
        baseRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        if (!bAlreadyAskedForStoragePermission) {

            bAlreadyAskedForStoragePermission = permissionUtils.requestPermissionsWithRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE_REQ_CODE,
                this
            )
        }
    }

    override fun onItemClick(selectedTrackPosition: Int) {
        tracksPresenter.startTrackPlayback(selectedTrackPosition)
    }

    override fun displayLibraryTracks(trackList: MutableList<Track>) {
        Log.d(TAG, "Loaded some tracks")
        listPresenter.setItemsList(trackList, adapter)
    }

    override fun displayEmptyLibrary() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onShowPermissionRationale(permission: String, requestCode: Int) {
        if (activity != null) {
            (activity as MainActivity).showPermissionRationale(permission, requestCode)
        }
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
                    onShowPermissionRationale(permissions[0], READ_EXTERNAL_STORAGE_REQ_CODE)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(
            KEY_STORAGE_PERMISSION_ALREADY_ASKED,
            bAlreadyAskedForStoragePermission
        )
        super.onSaveInstanceState(outState)
    }

    override fun getViewActivity(): AppCompatActivity {
        return activity as AppCompatActivity
    }

    override fun getApplicationContext(): Context {
        return (activity as AppCompatActivity).applicationContext
    }

    companion object {
        fun newInstance() = TracksLibraryFragment()
    }
}