package com.godzuche.achivitapp.feature.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.domain.repository.DarkThemeConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.settingsUiState.collect { settingsUiState ->

                    when (settingsUiState) {
                        SettingsUiState.Loading -> Unit

                        is SettingsUiState.Success -> {

                            findPreference<Preference>("key_notifications")
                                ?.setOnPreferenceClickListener {
                                    findNavController().navigate(
                                        SettingsPreferenceFragmentDirections.actionSettingsPreferenceFragmentToActionNotificationsSettings()
                                    )
                                    true
                                }

                            findPreference<Preference>("key_colors")
                                ?.setOnPreferenceClickListener {
                                    findNavController().navigate(
                                        SettingsPreferenceFragmentDirections.actionSettingsPreferenceFragmentToActionColorsSettings()
                                    )
                                    true
                                }

                            findPreference<Preference>("key_your_account")
                                ?.setOnPreferenceClickListener {
                                    findNavController().navigate(
                                        SettingsPreferenceFragmentDirections.actionSettingsPreferenceFragmentToAccountPrefFragment()
                                    )
                                    true
                                }

                            findPreference<ListPreference>("key_dark_mode")
                                ?.apply {
                                    preferenceDataStore = object : PreferenceDataStore() {

                                        override fun putString(key: String?, value: String?) {
                                            val darkThemeConfig =
                                                value?.getDarkThemeConfig()
                                                    ?: DarkThemeConfig.FOLLOW_SYSTEM
                                            settingsViewModel.updateDarkThemeConfig(darkThemeConfig)
                                        }

                                        override fun getString(
                                            key: String?,
                                            defValue: String?
                                        ): String? {
                                            return settingsUiState.settings.darkThemeConfig.getDarkMode()
                                        }

                                    }
                                }
                        }
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE
    }
}