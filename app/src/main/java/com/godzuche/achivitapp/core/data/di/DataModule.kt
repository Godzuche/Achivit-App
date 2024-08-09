package com.godzuche.achivitapp.core.data.di

import com.godzuche.achivitapp.core.data.repository.DefaultAuthRepository
import com.godzuche.achivitapp.core.data.repository.DefaultNotificationRepository
import com.godzuche.achivitapp.core.data.repository.DefaultRecentSearchRepository
import com.godzuche.achivitapp.core.data.repository.DefaultSearchContentsRepository
import com.godzuche.achivitapp.core.data.repository.DefaultUserDataRepository
import com.godzuche.achivitapp.core.data.repository.OfflineFirstTaskRepository
import com.godzuche.achivitapp.core.data.repository.TaskCategoryRepositoryImpl
import com.godzuche.achivitapp.core.data.repository.TaskCollectionRepositoryImpl
import com.godzuche.achivitapp.core.data.util.connectivity.ConnectivityManagerNetworkMonitor
import com.godzuche.achivitapp.core.data.util.alarm.DueTaskAndroidAlarmScheduler
import com.godzuche.achivitapp.core.domain.repository.AuthRepository
import com.godzuche.achivitapp.core.domain.repository.NotificationRepository
import com.godzuche.achivitapp.core.domain.repository.RecentSearchRepository
import com.godzuche.achivitapp.core.domain.repository.SearchContentsRepository
import com.godzuche.achivitapp.core.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.core.domain.repository.TaskCollectionRepository
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import com.godzuche.achivitapp.core.domain.repository.UserDataRepository
import com.godzuche.achivitapp.core.domain.util.DueTaskAlarmScheduler
import com.godzuche.achivitapp.core.domain.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsTaskRepository(
        taskRepository: OfflineFirstTaskRepository
    ): TaskRepository

    @Binds
    fun bindsTaskCollectionRepository(
        taskCollectionRepository: TaskCollectionRepositoryImpl
    ): TaskCollectionRepository

    @Binds
    fun bindsTaskCategoryRepository(
        taskCategoryRepository: TaskCategoryRepositoryImpl
    ): TaskCategoryRepository

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: DefaultUserDataRepository
    ): UserDataRepository

    @Binds
    fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository
    ): RecentSearchRepository

    @Binds
    fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository
    ): SearchContentsRepository

    @Binds
    fun bindsNotificationRepository(
        notificationRepository: DefaultNotificationRepository
    ): NotificationRepository

    @Binds
    fun bindsDueTaskAlarmScheduler(
        dueTaskAlarmScheduler: DueTaskAndroidAlarmScheduler
    ): DueTaskAlarmScheduler

    @Binds
    fun bindsAuthRepository(
        authRepository: DefaultAuthRepository
    ): AuthRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}