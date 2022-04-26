package com.godzuche.achivitapp.feature_task.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskEntity

@Database(entities = [TaskCategoryEntity::class, TaskCollectionEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val categoryDao: TaskCategoryDao
    abstract val collectionDao: TaskCollectionDao

    /* companion object {
         @Volatile
         private var INSTANCE: TaskRoomDatabase? = null

         fun getDatabase(context: Context): TaskRoomDatabase {
             return INSTANCE ?: synchronized(this) {
                 val instance = Room.databaseBuilder(
                     context,
                     TaskRoomDatabase::class.java,
                     "task_database"
                 )
                     .fallbackToDestructiveMigration()
                     .build()
                 INSTANCE = instance
                 return instance
             }
         }
     }
 */
}