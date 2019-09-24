package com.prush.justanotherplayer.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.prush.justanotherplayer.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}