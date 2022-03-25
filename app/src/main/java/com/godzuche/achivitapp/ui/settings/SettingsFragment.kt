package com.godzuche.achivitapp.ui.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.godzuche.achivitapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Notifications pref
        findPreference<Preference>("key_notifications")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionActionSettingsToActionNotificationsSettings())
                true
            }

        // Colors pref
        findPreference<Preference>("key_colors")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionActionSettingsToActionColorsSettings())
                true
            }

        // Your account pref
        findPreference<Preference>("key_your_account")
            ?.setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionActionSettingsToAccountPrefFragment())
                true
            }

    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<Chip>(R.id.chip_add_collection)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }
}