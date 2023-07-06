package com.godzuche.achivitapp.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentSettingsBinding
import com.google.android.material.transition.MaterialSharedAxis

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.preferences_container_view) as NavHostFragment
        val navController = navHostFragment.navController
        binding.toolbar.setupWithNavController(findNavController())

        /*      if (childFragmentManager.findFragmentById(R.id.preferences_container_view) == null) {
                  childFragmentManager.commitNow {
                      replace(R.id.preferences_container_view, SettingsPreferenceFragment())
                  }
              }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}