package com.prush.justanotherplayer.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseNowPlayingActivity
import com.prush.justanotherplayer.utils.PermissionCallbacks
import com.prush.justanotherplayer.utils.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*

private const val KEY_STORAGE_PERMISSION_ALREADY_ASKED = "storagePermissionAlreadyAsked"
const val READ_EXTERNAL_STORAGE_REQ_CODE: Int = 101

class MainActivity : BaseNowPlayingActivity(), PermissionCallbacks {

    private lateinit var permissionUtils: PermissionUtils
    private var bAlreadyAskedForStoragePermission: Boolean = false

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        super.onViewCreated(savedInstanceState)

        permissionUtils = PermissionUtils()

        if (savedInstanceState != null) {
            bAlreadyAskedForStoragePermission =
                savedInstanceState.getBoolean(KEY_STORAGE_PERMISSION_ALREADY_ASKED, false)
        }
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

    override fun onShowPermissionRationale(permission: String, requestCode: Int) {

        Snackbar.make(rootLayout, R.string.permissions_rationale, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.okay) {
                PermissionUtils().requestPermission(this, permission, requestCode)
            }
            .show()
    }

    override fun onPermissionGranted(permission: String) {
        setupViewPager()
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
                    setupViewPager()
                } else {
                    onShowPermissionRationale(permissions[0], READ_EXTERNAL_STORAGE_REQ_CODE)
                }
            }
        }
    }

    private fun setupViewPager() {

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 3

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(
            KEY_STORAGE_PERMISSION_ALREADY_ASKED,
            bAlreadyAskedForStoragePermission
        )
        super.onSaveInstanceState(outState)
    }

}
