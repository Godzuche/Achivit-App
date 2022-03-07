package com.godzuche.achivitapp

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup

class ColorPrefFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.colors_preference, rootKey)
    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }

}