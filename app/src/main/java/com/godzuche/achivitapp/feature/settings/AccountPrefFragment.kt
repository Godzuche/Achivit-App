package com.godzuche.achivitapp.feature.settings

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.godzuche.achivitapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AccountPrefFragment : PreferenceFragmentCompat() {

    override fun onStart() {
        super.onStart()

        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<Chip>(R.id.chip_add_category)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey)

    }
}