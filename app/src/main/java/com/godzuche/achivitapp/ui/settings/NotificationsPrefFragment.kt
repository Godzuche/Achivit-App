package com.godzuche.achivitapp.ui.settings

import android.os.Bundle
import android.view.View
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.godzuche.achivitapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup

class NotificationsPrefFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notifications_settings, rootKey)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Dependency of the visibility of the vibrate switch on the notify switch
        findPreference<SwitchPreferenceCompat>("key_vibrate")
            ?.isVisible = sharedPref.getBoolean("key_notify_device", false)

        findPreference<SwitchPreferenceCompat>("key_notify_device")
            ?.setOnPreferenceChangeListener { _, newValue ->

                findPreference<SwitchPreferenceCompat>("key_vibrate")
                    ?.isVisible = newValue as Boolean

                findPreference<MultiSelectListPreference>("key_filter_push_notifications")
                    ?.isVisible = newValue as Boolean

                true
            }

        // Dependency of the visibility of the filter push notifications on the notify switch
        findPreference<MultiSelectListPreference>("key_filter_push_notifications")
            ?.isVisible = sharedPref.getBoolean("key_notify_device", false)

    }


    override fun onStart() {
        super.onStart()
        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }

}