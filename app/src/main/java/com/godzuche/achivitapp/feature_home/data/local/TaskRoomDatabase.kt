package com.godzuche.achivitapp.feature_home.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskEntity

@Database(
    entities = [TaskCategoryEntity::class, TaskCollectionEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val categoryDao: TaskCategoryDao
    abstract val collectionDao: TaskCollectionDao

}