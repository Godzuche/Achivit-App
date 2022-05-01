package com.godzuche.achivitapp.feature_task.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.godzuche.achivitapp.feature_task.data.local.TaskCategoryDao
import com.godzuche.achivitapp.feature_task.data.local.TaskCollectionDao
import com.godzuche.achivitapp.feature_task.data.local.TaskRoomDatabase
import com.godzuche.achivitapp.feature_task.data.repository.TaskRepositoryImpl
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.domain.use_case.GetTask
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Provides
    @Singleton
    fun provideScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    private val categoryContentValues = ContentValues().apply {
//        put("categoryId", 0L)
        put("title", "My Tasks")
    }
    private val collectionContentValues = ContentValues().apply {
//        put("collectionId", 0L)
        put("title", "All Tasks")
        put("category_title", "My Tasks")
    }

    @Provides
    @Singleton
    fun getTaskDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope,
    ): TaskRoomDatabase {
        return Room.databaseBuilder(
            context,
            TaskRoomDatabase::class.java,
            "task.db"
        ).fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    scope.launch {
                        db.insert("task_categories", CONFLICT_REPLACE, categoryContentValues)
                        db.insert("task_collections", CONFLICT_REPLACE, collectionContentValues)

                    }
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(db: TaskRoomDatabase): TaskCategoryDao = db.categoryDao

    @Provides
    @Singleton
    fun provideCollectionDao(db: TaskRoomDatabase): TaskCollectionDao = db.collectionDao

    @Provides
    @Singleton
    fun getTaskRepository(db: TaskRoomDatabase): TaskRepository {
        return TaskRepositoryImpl(db.taskDao, db.categoryDao, db.collectionDao)
    }

    @Provides
    @Singleton
    fun getTaskUseCase(repository: TaskRepository): GetTask {
        return GetTask(repository)
    }

}