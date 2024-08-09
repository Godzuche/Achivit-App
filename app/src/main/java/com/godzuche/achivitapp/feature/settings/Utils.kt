package com.godzuche.achivitapp.feature.settings

import androidx.appcompat.app.AppCompatDelegate
import com.godzuche.achivitapp.core.domain.repository.DarkThemeConfig

fun setDarkMode(darkMode: String?) {
    when (darkMode) {
        DarkMode.light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        DarkMode.dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        DarkMode.followSystemDefault -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

object DarkMode {
    const val light = "light_mode"
    const val dark = "dark_mode"
    const val followSystemDefault = "system_default"
}

fun String.getDarkThemeConfig(): DarkThemeConfig? =
    when (this) {
        DarkMode.dark -> DarkThemeConfig.DARK
        DarkMode.light -> DarkThemeConfig.LIGHT
        DarkMode.followSystemDefault -> DarkThemeConfig.FOLLOW_SYSTEM
        else -> null
    }

fun DarkThemeConfig.getDarkMode(): String =
    when (this) {
        DarkThemeConfig.DARK -> DarkMode.dark
        DarkThemeConfig.LIGHT -> DarkMode.light
        DarkThemeConfig.FOLLOW_SYSTEM -> DarkMode.followSystemDefault
    }