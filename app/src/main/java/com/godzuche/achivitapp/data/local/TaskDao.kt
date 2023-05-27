package com.godzuche.achivitapp.data.local

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.godzuche.achivitapp.data.local.entity.TaskEntity
import com.godzuche.achivitapp.feature.feed.util.TaskStatus
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

    @Query("SELECT * FROM task_table ORDER BY due_date DESC, id DESC")
    fun getPagedTasks(): PagingSource<Int, TaskEntity>

    @Query(
        "SELECT * FROM task_table WHERE " +
                " status = :status ORDER BY due_date DESC, id DESC"
    )
    fun getFilteredPagedTasks(
        status: TaskStatus
    ): PagingSource<Int, TaskEntity>

    @Query(
        "SELECT * FROM task_table WHERE " +
                " category_title = :category ORDER BY due_date DESC, id DESC"
    )
    fun getFilteredPagedTasks(
        category: String
    ): PagingSource<Int, TaskEntity>

    @Query(
        "SELECT * FROM task_table WHERE " +
                " category_title = :category AND status = :status " +
                "ORDER BY due_date DESC, id DESC"
    )
    fun getFilteredPagedTasks(
        category: String,
        status: TaskStatus
    ): PagingSource<Int, TaskEntity>

    @Query(
        "SELECT * FROM task_table WHERE " +
                " category_title = :category AND collection_title = :collection " +
                "ORDER BY due_date DESC, id DESC"
    )
    fun getFilteredPagedTasks(
        category: String,
        collection: String
    ): PagingSource<Int, TaskEntity>

    @Query(
        "SELECT * FROM task_table WHERE " +
                " category_title = :category AND collection_title = :collection AND status = :status " +
                "ORDER BY due_date DESC, id DESC"
    )
    fun getFilteredPagedTasks(
        category: String,
        collection: String,
        status: TaskStatus
    ): PagingSource<Int, TaskEntity>

    @Query(
        "SELECT * FROM task_table WHERE " +
                " title LIKE '%' || :title || '%' OR description LIKE '%' || :title || '%'" +
                "ORDER BY title ASC"
    )
    suspend fun searchTasksByTitle(title: String): List<TaskEntity>

    @Query("SELECT * FROM task_table ORDER BY due_date DESC, id DESC")
    fun getTodayTasks(): Flow<List<TaskEntity>>

    @RawQuery(observedEntities = [TaskEntity::class])
    fun getFilteredTasks(query: SupportSQLiteQuery): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task_table WHERE status = :status ORDER BY due_date DESC, id DESC")
    fun getTaskByStatus(status: TaskStatus): Flow<List<TaskEntity>>

}