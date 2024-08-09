package com.godzuche.achivitapp.core.data.repository

import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.core.data.local.datastore.AchivitPreferencesDataSource
import com.godzuche.achivitapp.core.data.local.datastore.UserData
import com.godzuche.achivitapp.core.data.local.datastore.model.TaskFilter
import com.godzuche.achivitapp.core.domain.repository.DarkThemeConfig
import com.godzuche.achivitapp.core.domain.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultUserDataRepository @Inject constructor(
    private val achivitPreferencesDataSource: AchivitPreferencesDataSource,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : UserDataRepository {

    override val userData: Flow<UserData> =
        achivitPreferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        withContext(ioDispatcher) {
            achivitPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        withContext(ioDispatcher) {
            achivitPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
        }
    }

    override suspend fun setTaskFilter(taskFilter: TaskFilter) {
        withContext(ioDispatcher) {
            achivitPreferencesDataSource.setTaskFilter(taskFilter)
        }
    }
}