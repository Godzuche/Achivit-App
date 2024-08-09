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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val categoryContentValue = ContentValues().apply {
        put("title", "My Tasks")
        put("created", Calendar.getInstance().timeInMillis)
    }

    val collectionContentValue = ContentValues().apply {
        put("title", "All Tasks")
        put("category_title", "My Tasks")
    }

    @Provides
    @Singleton
    fun providesAchivitDatabase(
        @ApplicationContext context: Context,
        @Dispatcher(AchivitDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): AchivitDatabase = Room.databaseBuilder(
        context,
        AchivitDatabase::class.java,
        "achivit-database"
    ).addCallback(
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val scope = CoroutineScope(ioDispatcher)
                scope.launch {
//                    async {
                        db.insert(
                            table = "task_categories",
                            conflictAlgorithm = CONFLICT_REPLACE,
                            values = categoryContentValue
                        )

                        db.insert(
                            table = "task_collections",
                            conflictAlgorithm = CONFLICT_REPLACE,
                            values = collectionContentValue
                        )
//                    }.await()
                }
            }
        }
    )
        .build()
}