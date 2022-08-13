package com.godzuche.achivitapp

import android.app.Application
import androidx.preference.PreferenceManager
import com.godzuche.achivitapp.feature_settings.setDarkMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskApplication : Application() {
    //    private val applicationScope = CoroutineScope(SupervisorJob())
//    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
        // Set up the dark mode preference on cold start
        setUpDarkModePreference()
    }

    private fun setUpDarkModePreference() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val defaultValue = resources.getStringArray(R.array.entry_value_theme)[0]
        val darkMode = sharedPref.getString("key_dark_mode", defaultValue)
        setDarkMode(darkMode)
    }
}