package com.godzuche.achivitapp

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.chip.ChipGroup

class SettingsFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
    }
}