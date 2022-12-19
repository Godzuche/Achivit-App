package com.godzuche.achivitapp.feature_task.data.local

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAndGetId(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun reInsert(task: TaskEntity)

    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getLastInsertedTask(): Flow<List<TaskEntity>>

    @Delete
    suspend fun delete(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("SELECT * FROM task_table WHERE id = :id")
    fun getTask(id: Int): Flow<TaskEntity>

    @Query("SELECT * FROM task_table WHERE id = :id")
    fun getTaskOnce(id: Int): TaskEntity

    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getAllTasks(): PagingSource<Int, TaskEntity>

    @Query(
        "SELECT * FROM task_table WHERE " +
                " title LIKE '%' || :title || '%' OR description LIKE '%' || :title || '%'" +
                "ORDER BY title ASC"
    )
    suspend fun searchTasksByTitle(title: String): List<TaskEntity>

    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<TaskEntity>>

    @RawQuery(observedEntities = [TaskEntity::class])
    fun getFilteredTasks(query: SupportSQLiteQuery): Flow<List<TaskEntity>>

}