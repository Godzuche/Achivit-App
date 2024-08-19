package com.godzuche.achivitapp.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.godzuche.achivitapp.core.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.database.relations.CategoryWithCollectionsAndTasksEntities
import com.godzuche.achivitapp.core.data.local.database.relations.CategoryWithCollectionsEntities
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
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

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.CATEGORY_TABLE_NAME}
            ORDER BY ${TaskCategoryEntity.COLUMN_TITLE}
        """
    )
    fun getAllCategories(): Flow<List<TaskCategoryEntity>>

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.CATEGORY_TABLE_NAME} 
            ORDER BY ${TaskCategoryEntity.COLUMN_TITLE}
        """
    )
    fun retrieveAllCategories(): List<TaskCategoryEntity>

    @Query(
        """
            SELECT * FROM ${DatabaseConstants.CATEGORY_TABLE_NAME}
            WHERE ${TaskCategoryEntity.COLUMN_TITLE} = :title
        """
    )
    fun getCategory(title: String): Flow<TaskCategoryEntity>

    @Transaction
    @Query("SELECT * FROM ${DatabaseConstants.CATEGORY_TABLE_NAME}")
    fun getCategoriesWithCollections(): Flow<List<CategoryWithCollectionsEntities>>

    @Transaction
    @Query(
        """
            SELECT * FROM ${DatabaseConstants.CATEGORY_TABLE_NAME}
            WHERE ${TaskCategoryEntity.COLUMN_TITLE} = :categoryTitle
        """
    )
    fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollectionsEntities>>

    @Transaction
    @Query("SELECT * FROM ${DatabaseConstants.CATEGORY_TABLE_NAME}")
    fun getCategoryWithCollectionsAndTasks(): Flow<List<CategoryWithCollectionsAndTasksEntities>>

    @Transaction
    @Query("SELECT * FROM ${DatabaseConstants.CATEGORY_TABLE_NAME}")
    fun retrieveCategoryWithCollectionsAndTasks(): List<CategoryWithCollectionsAndTasksEntities>

    @Query("DELETE FROM ${DatabaseConstants.CATEGORY_TABLE_NAME}")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategories(entities: List<TaskCategoryEntity>)
}