package com.godzuche.achivitapp.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.godzuche.achivitapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ColorPrefFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.colors_preference, rootKey)
    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }

}