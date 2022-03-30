package com.godzuche.achivitapp.feature_task.di

import android.app.Application
import androidx.room.Room
import com.godzuche.achivitapp.feature_task.data.TaskRepositoryImpl
import com.godzuche.achivitapp.feature_task.data.local.TaskRoomDatabase
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.domain.use_case.GetTask
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Provides
    @Singleton
    fun getTaskDatabase(app: Application): TaskRoomDatabase {
        return Room.databaseBuilder(
            app,
            TaskRoomDatabase::class.java,
            "task_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun getTaskRepository(db: TaskRoomDatabase): TaskRepository {
        return TaskRepositoryImpl(db.taskDao)
    }

    @Provides
    @Singleton
    fun getTaskUseCase(repository: TaskRepository): GetTask {
        return GetTask(repository)
    }

}