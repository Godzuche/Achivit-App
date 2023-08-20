package com.godzuche.achivitapp.data.local.database.dao

import androidx.room.*
import com.godzuche.achivitapp.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollectionsAndTasksEntities
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollectionsEntities
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
    fun getAllCategories(): Flow<List<TaskCategoryEntity>>

    @Query(
        """
            SELECT * FROM task_categories ORDER BY title
        """
    )
    fun retrieveAllCategories(): List<TaskCategoryEntity>

    @Query("SELECT * FROM task_categories WHERE title = :title")
    fun getCategory(title: String): Flow<TaskCategoryEntity>

    @Transaction
    @Query("SELECT * FROM task_categories")
    fun getCategoriesWithCollections(): Flow<List<CategoryWithCollectionsEntities>>

    @Transaction
    @Query("SELECT * FROM task_categories WHERE title = :categoryTitle")
    fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollectionsEntities>>

    @Transaction
    @Query("SELECT * FROM task_categories")
    fun getCategoryWithCollectionsAndTasks(): Flow<List<CategoryWithCollectionsAndTasksEntities>>

    @Transaction
    @Query("SELECT * FROM task_categories")
    fun retrieveCategoryWithCollectionsAndTasks(): List<CategoryWithCollectionsAndTasksEntities>

    @Query("DELETE FROM task_categories")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertCategories(entities: List<TaskCategoryEntity>)
}