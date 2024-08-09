package com.godzuche.achivitapp.core.data.local.database.di

import com.godzuche.achivitapp.core.data.local.database.AchivitDatabase
import com.godzuche.achivitapp.core.data.local.database.dao.NotificationDao
import com.godzuche.achivitapp.core.data.local.database.dao.RecentSearchQueryDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskCategoryDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskCollectionDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskFtsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesTaskDao(
        database: AchivitDatabase
    ): TaskDao = database.taskDao()

    @Provides
    fun providesTaskCollectionDao(
        database: AchivitDatabase
    ): TaskCollectionDao = database.taskCollectionDao()

    @Provides
    fun providesTaskCategoryDao(
        database: AchivitDatabase
    ): TaskCategoryDao = database.taskCategoryDao()

    @Provides
    fun providesRecentSearchQueryDao(
        database: AchivitDatabase
    ): RecentSearchQueryDao = database.recentSearchQueryDao()

    @Provides
    fun providesTaskFtsDao(
        database: AchivitDatabase
    ): TaskFtsDao = database.taskFtsDao()

    @Provides
    fun providesNotificationDao(
        database: AchivitDatabase
    ): NotificationDao = database.notificationDao()
}