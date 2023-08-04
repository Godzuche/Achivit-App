package com.godzuche.achivitapp.data.di

import com.godzuche.achivitapp.data.repository.DefaultAuthRepository
import com.godzuche.achivitapp.data.repository.DefaultNotificationRepository
import com.godzuche.achivitapp.data.repository.DefaultRecentSearchRepository
import com.godzuche.achivitapp.data.repository.DefaultSearchContentsRepository
import com.godzuche.achivitapp.data.repository.DefaultUserDataRepository
import com.godzuche.achivitapp.data.repository.TaskCategoryRepositoryImpl
import com.godzuche.achivitapp.data.repository.TaskCollectionRepositoryImpl
import com.godzuche.achivitapp.data.repository.TaskRepositoryImpl
import com.godzuche.achivitapp.data.util.ConnectivityManagerNetworkMonitor
import com.godzuche.achivitapp.data.util.DueTaskAndroidAlarmScheduler
import com.godzuche.achivitapp.domain.repository.AuthRepository
import com.godzuche.achivitapp.domain.repository.NotificationRepository
import com.godzuche.achivitapp.domain.repository.RecentSearchRepository
import com.godzuche.achivitapp.domain.repository.SearchContentsRepository
import com.godzuche.achivitapp.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.domain.repository.TaskCollectionRepository
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.domain.repository.UserDataRepository
import com.godzuche.achivitapp.domain.util.DueTaskAlarmScheduler
import com.godzuche.achivitapp.domain.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsTaskRepository(
        taskRepository: TaskRepositoryImpl
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