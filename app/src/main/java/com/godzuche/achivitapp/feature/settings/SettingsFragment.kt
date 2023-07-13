package com.godzuche.achivitapp.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.R
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(/*R.layout.fragment_settings*/) {
//    private lateinit var binding: FragmentSettingsBinding

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
        /*binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root*/
        return ComposeView(requireContext()).apply {
            id = R.id.settings_fragment
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                Mdc3Theme(
                    context = this@SettingsFragment.requireContext(),
                    setDefaultFontFamily = true
                ) {
                    SettingsRoute(
                        onNavigateUp = {
                            findNavController().navigateUp()
                        }
                    )
                }
            }
        }
    }

    /*    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.preferences_container_view) as NavHostFragment
            val navController = navHostFragment.navController
            binding.toolbar.setupWithNavController(findNavController())

            *//*      if (childFragmentManager.findFragmentById(R.id.preferences_container_view) == null) {
                  childFragmentManager.commitNow {
                      replace(R.id.preferences_container_view, SettingsPreferenceFragment())
                  }
              }*//*
    }*/
}