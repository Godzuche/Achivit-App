package com.godzuche.achivitapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpDarkModePreference()
    }

    private fun setUpDarkModePreference() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val defaultValue = false
        val isDarkMode = sharedPref.getBoolean("key_dark_mode", defaultValue)
        if (isDarkMode) setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}