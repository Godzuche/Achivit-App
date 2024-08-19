package com.godzuche.achivitapp.core.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import com.godzuche.achivitapp.core.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAndGetId(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun reInsert(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE id = :id")
    fun getTask(id: Int): Flow<TaskEntity>

    @Query("SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE id = :id")
    fun getOneOffTask(id: Int): TaskEntity

    @Query("SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} ORDER BY due_date DESC, id DESC")
    fun getPagedTasks(): PagingSource<Int, TaskEntity>

    @Query(
        value = """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME}
            WHERE id IN (:ids)
        """
    )
    fun getTaskEntitiesByIds(ids: Set<Int>): Flow<List<TaskEntity>>

    @Query(
        value = """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} 
            WHERE status = :status 
            ORDER BY due_date DESC, id DESC
                """,
    )
    fun getFilteredPagedTasks(
        status: TaskStatus
    ): PagingSource<Int, TaskEntity>

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE
            category_title = :category ORDER BY due_date DESC, id DESC
        """
    )
    fun getFilteredPagedTasks(
        category: String
    ): PagingSource<Int, TaskEntity>

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE
            category_title = :category AND status = :status
            ORDER BY due_date DESC, id DESC
        """
    )
    fun getFilteredPagedTasks(
        category: String,
        status: TaskStatus
    ): PagingSource<Int, TaskEntity>

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE
            category_title = :category AND collection_title = :collection
            ORDER BY due_date DESC, id DESC
        """
    )
    fun getFilteredPagedTasks(
        category: String,
        collection: String
    ): PagingSource<Int, TaskEntity>

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE
            category_title = :category AND collection_title = :collection AND status = :status
            ORDER BY due_date DESC, id DESC
        """
    )
    fun getFilteredPagedTasks(
        category: String,
        collection: String,
        status: TaskStatus
    ): PagingSource<Int, TaskEntity>

    @Query(
        value = """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE 
            title LIKE '%' || :title || '%' OR description LIKE '%' || :title || '%'
            ORDER BY title ASC
            """
    )
    suspend fun searchTasksByTitle(title: String): List<TaskEntity>

    @Query("SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME} ORDER BY due_date DESC, id DESC")
    fun getTodayTasks(): Flow<List<TaskEntity>>

    @RawQuery(observedEntities = [TaskEntity::class])
    fun getFilteredTasks(query: SupportSQLiteQuery): Flow<List<TaskEntity>>

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.TASK_TABLE_NAME}
            WHERE status = :status ORDER BY due_date DESC, id DESC
        """
    )
    fun getTaskByStatus(status: TaskStatus): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasks(entities: List<TaskEntity>)

    @Query("SELECT count(*) FROM ${DatabaseConstants.TASK_TABLE_NAME} WHERE status = :status")
    fun getTasksCountByStatus(status: TaskStatus): Flow<Int>

}