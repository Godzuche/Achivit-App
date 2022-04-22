package com.godzuche.achivitapp.feature_task.data.local

import androidx.room.*
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCollectionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(collection: TaskCollectionEntity)

    @Update
    suspend fun update(collection: TaskCollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: TaskCollectionEntity)

    @Query("SELECT * FROM task_categories ORDER BY id")
    fun getAllCategory(): Flow<List<TaskCollectionEntity>>

    @Query("SELECT * FROM task_categories WHERE id = :id")
    fun getCategory(id: Long): Flow<TaskCollectionEntity>
}