package com.prush.justanotherplayer.utils

import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private val TAG = "PermissionUtils"

interface PermissionCallbacks {

    fun onShowPermissionRationale(permission: String)
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
                permissionCallbacks.onShowPermissionRationale(permission)
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

    fun requestPermission(
        activity: AppCompatActivity,
        permission: String,
        requestCode: Int
    ) {

        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
}