package com.godzuche.achivitapp.core.data.local.database.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.icu.util.Calendar
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.core.data.local.database.AchivitDatabase
import com.godzuche.achivitapp.core.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val categoryContentValue = ContentValues().apply {
        put(
            TaskCategoryEntity.COLUMN_TITLE,
            DatabaseConstants.PrepopulatedData.DEFAULT_CATEGORY_TITLE,
        )
        put(TaskCategoryEntity.COLUMN_CREATED, Calendar.getInstance().timeInMillis)
    }

    val collectionContentValue = ContentValues().apply {
        put(
            TaskCollectionEntity.COLUMN_TITLE,
            DatabaseConstants.PrepopulatedData.DEFAULT_COLLECTION_TITLE,
        )
        put(
            TaskCollectionEntity.COLUMN_CATEGORY_TITLE,
            DatabaseConstants.PrepopulatedData.DEFAULT_CATEGORY_TITLE,
        )
    }

    @Provides
    @Singleton
    fun providesAchivitDatabase(
        @ApplicationContext context: Context,
        @Dispatcher(AchivitDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): AchivitDatabase = Room.databaseBuilder(
        context,
        AchivitDatabase::class.java,
        DatabaseConstants.DATABASE_NAME,
    ).addCallback(
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val scope = CoroutineScope(ioDispatcher)
                scope.launch {
                    db.insert(
                        table = DatabaseConstants.CATEGORY_TABLE_NAME,
                        conflictAlgorithm = CONFLICT_REPLACE,
                        values = categoryContentValue,
                    )

                    db.insert(
                        table = DatabaseConstants.COLLECTION_TABLE_NAME,
                        conflictAlgorithm = CONFLICT_REPLACE,
                        values = collectionContentValue,
                    )
                }
            }
        }
    )
        .build()
}