package com.godzuche.achivitapp.feature_task.data.local

import androidx.room.*
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: TaskCategoryEntity)

    @Update
    suspend fun update(category: TaskCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: TaskCategoryEntity)

    @Query("SELECT * FROM task_categories ORDER BY categoryId")
    fun getAllCategory(): Flow<List<TaskCategoryEntity>>

    @Query("SELECT * FROM task_categories WHERE categoryId = :id")
    fun getCategory(id: Long): Flow<TaskCategoryEntity>

    @Query("DELETE FROM task_categories")
    suspend fun deleteAll()
}