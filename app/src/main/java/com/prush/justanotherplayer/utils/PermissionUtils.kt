package com.prush.justanotherplayer.utils

import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.prush.justanotherplayer.ui.main.READ_EXTERNAL_STORAGE_REQ_CODE

private const val TAG = "PermissionUtils"

interface PermissionCallbacks {

    fun onShowPermissionRationale(permission: String, requestCode: Int)
    fun onPermissionGranted(permission: String)
}

class PermissionUtils {

    fun requestPermissionsWithRationale(
        activity: AppCompatActivity,
        permission: String,
        requestCode: Int,
        permissionCallbacks: PermissionCallbacks
    ): Boolean {

        Log.d(TAG, "Requesting permission for accessing storage.")

        return if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted. Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                permissionCallbacks.onShowPermissionRationale(
                    permission,
                    READ_EXTERNAL_STORAGE_REQ_CODE
                )
            } else {
                requestPermission(activity, permission, requestCode)
            }

            true //returning true suggesting that the permission has been asked successfully

        } else {

            //permission is granted
            permissionCallbacks.onPermissionGranted(permission)

            false //returning false suggesting that the permission request was not made as we already have permission
        }
    }

    fun requestPermission(activity: AppCompatActivity, permission: String, requestCode: Int) {

        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun requestPermissionsWithRationale(
        fragment: Fragment,
        permission: String,
        requestCode: Int,
        permissionCallbacks: PermissionCallbacks
    ): Boolean {

        Log.d(TAG, "Requesting permission for accessing storage.")

        return if (ContextCompat.checkSelfPermission(
                fragment.context!!, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted. Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.activity!!,
                    permission
                )
            ) {
                permissionCallbacks.onShowPermissionRationale(
                    permission,
                    READ_EXTERNAL_STORAGE_REQ_CODE
                )
            } else {
                requestPermission(fragment, permission, requestCode)
            }

            true //returning true suggesting that the permission has been asked successfully

        } else {

            //permission is granted
            permissionCallbacks.onPermissionGranted(permission)

            false //returning false suggesting that the permission request was not made as we already have permission
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun requestPermission(fragment: Fragment, permission: String, requestCode: Int) {

        fragment.requestPermissions(arrayOf(permission), requestCode)
    }


}