package com.godzuche.achivitapp.data.di

import com.godzuche.achivitapp.data.repository.DefaultNotificationRepository
import com.godzuche.achivitapp.data.repository.DefaultRecentSearchRepository
import com.godzuche.achivitapp.data.repository.DefaultSearchContentsRepository
import com.godzuche.achivitapp.data.repository.TaskCategoryRepositoryImpl
import com.godzuche.achivitapp.data.repository.TaskCollectionRepositoryImpl
import com.godzuche.achivitapp.data.repository.TaskRepositoryImpl
import com.godzuche.achivitapp.domain.repository.NotificationRepository
import com.godzuche.achivitapp.domain.repository.RecentSearchRepository
import com.godzuche.achivitapp.domain.repository.SearchContentsRepository
import com.godzuche.achivitapp.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.domain.repository.TaskCollectionRepository
import com.godzuche.achivitapp.domain.repository.TaskRepository
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
}