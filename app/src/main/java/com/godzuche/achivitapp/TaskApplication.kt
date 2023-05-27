package com.godzuche.achivitapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import com.godzuche.achivitapp.feature.notification.createDailyTaskNotificationChannel
import com.godzuche.achivitapp.feature.notification.createDueTaskNotificationChannel
import com.godzuche.achivitapp.feature.settings.setDarkMode
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class TaskApplication : Application(), Configuration.Provider {
    //    private val applicationScope = CoroutineScope(SupervisorJob())
//    private val applicationScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .setWorkerFactory(workerFactory)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.ERROR)
                .setWorkerFactory(workerFactory)
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        createDueTaskNotificationChannel()
        createDailyTaskNotificationChannel()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // Set up the dark mode preference on cold start
        setUpDarkModePreference()
    }

    private fun setUpDarkModePreference() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val defaultValue = resources.getStringArray(R.array.entry_value_theme)[0]
        val darkMode =
            sharedPref.getString(resources.getString(R.string.key_dark_mode), defaultValue)
        setDarkMode(darkMode)
    }
}