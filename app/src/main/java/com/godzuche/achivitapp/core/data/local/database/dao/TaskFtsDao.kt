package com.godzuche.achivitapp.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskFtsEntity
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskFtsEntity>)

    //    Optimized for FTS: FTS tables are designed for fast lookups, so fetching IDs first is efficient.
    @Query(
        value = """
            SELECT rowid FROM tasks_fts WHERE tasks_fts MATCH :query
            """
    )
    fun searchAllTasks(query: String): Flow<List<Int>>

    //    Joining tables can be expensive, especially with large datasets.
    //    This is retrieving all columns of TaskEntity even if you don't need them all for the search results.
    @Query(
        value = """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME}
            JOIN tasks_fts ON (${DatabaseConstants.TASK_TABLE_NAME}.id = tasks_fts.rowid) 
            WHERE tasks_fts MATCH :query
            """
    )
    fun searchAllTasks2(query: String): Flow<List<TaskEntity>>

    @Query(
        value = """
            SELECT count(*) FROM tasks_fts
        """
    )
    fun getCount(): Flow<Int>
}