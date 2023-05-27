package com.godzuche.achivitapp.data.local

import androidx.room.*
import com.godzuche.achivitapp.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.relations.CategoryWithCollections
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: TaskCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun reInsert(category: TaskCategoryEntity)

    @Update
    suspend fun update(category: TaskCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: TaskCategoryEntity)

    @Query("SELECT * FROM task_categories ORDER BY title")
    fun getAllCategory(): Flow<List<TaskCategoryEntity>>

    @Query("SELECT * FROM task_categories WHERE title = :title")
    fun getCategory(title: String): Flow<TaskCategoryEntity>

    @Transaction
    @Query("SELECT * FROM task_categories")
    fun getCategoriesWithCollections(): Flow<List<CategoryWithCollections>>

    @Transaction
    @Query("SELECT * FROM task_categories WHERE title = :categoryTitle")
    fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>>

    @Query("DELETE FROM task_categories")
    suspend fun deleteAll()
}