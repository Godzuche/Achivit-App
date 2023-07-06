package com.godzuche.achivitapp.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.godzuche.achivitapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountPrefFragment : PreferenceFragmentCompat() {

    override fun onStart() {
        super.onStart()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey)

    }
}