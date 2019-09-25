package com.prush.justanotherplayer.ui.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.R
import kotlinx.android.synthetic.main.activity_main.*

private const val SETTINGS_FRAGMENT_TAG = "SettingsFragment"

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.base_container_layout)

        val settingsFragment = supportFragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG)
                as SettingsFragment? ?: SettingsFragment.newInstance()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, settingsFragment, SETTINGS_FRAGMENT_TAG)
            commit()
        }

        toolbar.title = getString(R.string.settings)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}