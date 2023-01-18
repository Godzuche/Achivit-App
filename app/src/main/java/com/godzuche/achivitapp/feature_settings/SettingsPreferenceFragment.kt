package com.godzuche.achivitapp.feature_settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.godzuche.achivitapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<Preference>("key_notifications")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsPreferenceFragmentDirections.actionSettingsPreferenceFragmentToActionNotificationsSettings())
                true
            }

        findPreference<Preference>("key_colors")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsPreferenceFragmentDirections.actionSettingsPreferenceFragmentToActionColorsSettings())
                true
            }

        findPreference<Preference>("key_your_account")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsPreferenceFragmentDirections.actionSettingsPreferenceFragmentToAccountPrefFragment())
                true
            }

    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }
}