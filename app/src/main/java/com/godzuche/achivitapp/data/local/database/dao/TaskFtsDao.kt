package com.godzuche.achivitapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.godzuche.achivitapp.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.data.local.database.model.TaskFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskFtsEntity>)

    @Query(
        value = """
            SELECT rowid FROM tasks_fts WHERE tasks_fts MATCH :query
            """
    )
    fun searchAllTasks(query: String): Flow<List<Int>>

    @Query(
        value = """
            SELECT * FROM task_table
            JOIN tasks_fts ON (task_table.id = tasks_fts.rowid) 
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