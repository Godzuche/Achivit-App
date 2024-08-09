package com.godzuche.achivitapp.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.godzuche.achivitapp.BuildConfig
import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.core.domain.repository.UserDataRepository
import com.godzuche.achivitapp.feature.tasks.worker.createDailyTaskNotificationChannel
import com.godzuche.achivitapp.feature.tasks.worker.createDueTaskNotificationChannel
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AchivitApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    @Dispatcher(AchivitDispatchers.IO)
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = if (BuildConfig.DEBUG) {
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
        /*val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val defaultValue = resources.getStringArray(R.array.entry_value_theme)[2]
        val darkMode =
            sharedPref.getString(resources.getString(R.string.key_dark_mode), defaultValue)
        setDarkMode(darkMode)*/

        //
        /*CoroutineScope(ioDispatcher).launch {
            userDataRepository.userData.collectLatest {
                val darkThemeConfig = it.darkThemeConfig

                val darkMode = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkMode.followSystemDefault
                    DarkThemeConfig.DARK -> DarkMode.dark
                    DarkThemeConfig.LIGHT -> DarkMode.light
                }

                withContext(Dispatchers.Main) {
                    setDarkMode(darkMode = darkMode)
                }
            }
        }
   */
    }
}