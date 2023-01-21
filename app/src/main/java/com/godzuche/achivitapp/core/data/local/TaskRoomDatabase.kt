package com.godzuche.achivitapp.core.data.local

import androidx.room.BuiltInTypeConverters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.godzuche.achivitapp.core.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.core.data.local.entity.TaskEntity

@Database(
    entities = [TaskCategoryEntity::class, TaskCollectionEntity::class, TaskEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class, builtInTypeConverters = BuiltInTypeConverters(enums = BuiltInTypeConverters.State.DISABLED))
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val categoryDao: TaskCategoryDao
    abstract val collectionDao: TaskCollectionDao

}