package com.prush.justanotherplayer.ui.main

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseNowPlayingFooterActivity
import com.prush.justanotherplayer.utils.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseNowPlayingFooterActivity() {

    override fun getViewActivity(): AppCompatActivity {
        return this
    }

    override fun getContext(): Context {
        return applicationContext
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        super.onViewCreated(savedInstanceState)

        setupViewPager()
    }

    private fun setupViewPager() {

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 3

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun displayError() {
        Snackbar.make(rootLayout, R.string.error_sdcard, Snackbar.LENGTH_SHORT).show()
    }

    override fun showPermissionRationale(permission: String, requestCode: Int) {
        Snackbar.make(
            rootLayout,
            R.string.permissions_rationale, Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.okay) {
                PermissionUtils().requestPermission(this, permission, requestCode)
            }
            .show()
    }

}
