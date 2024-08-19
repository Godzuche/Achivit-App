package com.godzuche.achivitapp.core.data.local.database

import androidx.room.BuiltInTypeConverters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.godzuche.achivitapp.core.data.local.database.dao.NotificationDao
import com.godzuche.achivitapp.core.data.local.database.dao.RecentSearchQueryDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskCategoryDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskCollectionDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskFtsDao
import com.godzuche.achivitapp.core.data.local.database.model.NotificationEntity
import com.godzuche.achivitapp.core.data.local.database.model.RecentSearchQueryEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskFtsEntity
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import com.godzuche.achivitapp.core.data.local.database.util.InstantConverter
import com.godzuche.achivitapp.core.data.local.database.util.TaskStatusConverter

@Database(
    entities = [
        TaskCategoryEntity::class,
        TaskCollectionEntity::class,
        TaskEntity::class,
        TaskFtsEntity::class,
        RecentSearchQueryEntity::class,
        NotificationEntity::class
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false,
)
@TypeConverters(
    TaskStatusConverter::class,
    InstantConverter::class,
    builtInTypeConverters = BuiltInTypeConverters(enums = BuiltInTypeConverters.State.DISABLED)
)
abstract class AchivitDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun taskCategoryDao(): TaskCategoryDao
    abstract fun taskCollectionDao(): TaskCollectionDao
    abstract fun recentSearchQueryDao(): RecentSearchQueryDao
    abstract fun taskFtsDao(): TaskFtsDao
    abstract fun notificationDao(): NotificationDao

}