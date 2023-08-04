package com.prush.justanotherplayer.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.prush.justanotherplayer.BuildConfig
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.utils.PRIVACY_POLICY_URL


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val licensesPreference = findPreference<Preference>("licenses")
        licensesPreference?.setOnPreferenceClickListener {
            val intent = Intent(activity, OssLicensesMenuActivity::class.java)
            startActivity(intent)
            true
        }

        val privacyPolicyPreference = findPreference<Preference>("privacyPolicy")
        privacyPolicyPreference?.setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
            startActivity(browserIntent)
            true
        }
        val versionPreference = findPreference<Preference>("version")
        try {
            versionPreference?.summary = BuildConfig.VERSION_NAME
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}