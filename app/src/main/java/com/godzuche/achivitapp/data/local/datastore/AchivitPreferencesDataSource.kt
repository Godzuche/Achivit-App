package com.godzuche.achivitapp.data.local.datastore

import androidx.datastore.core.DataStore
import com.godzuche.achivitapp.data.local.datastore.model.TaskFilter
import com.godzuche.achivitapp.data.local.datastore.model.UserPreferences
import com.godzuche.achivitapp.domain.repository.DarkThemeConfig
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchivitPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData = userPreferences.data
        .map {
            UserData(
                darkThemeConfig = it.darkThemeConfig,
                taskFilter = it.taskFilter,
                shouldHideOnboarding = it.shouldHideOnboarding
            )
        }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy(
                darkThemeConfig = darkThemeConfig
            )
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy(
                shouldHideOnboarding = shouldHideOnboarding
            )
        }
    }

    suspend fun setTaskFilter(taskFilter: TaskFilter) {
        userPreferences.updateData {
            it.copy(
                taskFilter = taskFilter
            )
        }
    }

}

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val taskFilter: TaskFilter,
    val shouldHideOnboarding: Boolean
)