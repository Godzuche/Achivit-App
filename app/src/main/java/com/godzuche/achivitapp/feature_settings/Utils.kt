package com.godzuche.achivitapp.feature_settings

import androidx.appcompat.app.AppCompatDelegate

fun setDarkMode(darkMode: String?) {
    when (darkMode) {
        "light_mode" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        "dark_mode" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        "system_default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}