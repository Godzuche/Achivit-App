package com.godzuche.achivitapp

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Dependency of the vibrate switch on the notify switch
        findPreference<SwitchPreferenceCompat>("key_vibrate")
            ?.isVisible = sharedPref.getBoolean("key_notify_device", false)

        findPreference<SwitchPreferenceCompat>("key_notify_device")
            ?.setOnPreferenceChangeListener { _, newValue ->

                findPreference<SwitchPreferenceCompat>("key_vibrate")
                    ?.isVisible = newValue as Boolean

                true
            }

        findPreference<Preference>("key_notifications")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionActionSettingsToActionNotificationsSettings())
                true
            }

        findPreference<Preference>("key_colors")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionActionSettingsToActionColorsSettings())
                true
            }

        findPreference<Preference>("key_your_account")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionActionSettingsToAccountPrefFragment())
                true
            }

    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }
}