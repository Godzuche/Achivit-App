package com.godzuche.achivitapp.domain.repository

import com.godzuche.achivitapp.data.local.datastore.UserData
import com.godzuche.achivitapp.data.local.datastore.model.TaskFilter
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    val userData: Flow<UserData>

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)

    suspend fun setTaskFilter(taskFilter: TaskFilter)
}

enum class DarkThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}