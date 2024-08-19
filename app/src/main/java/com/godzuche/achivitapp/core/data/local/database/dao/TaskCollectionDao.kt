package com.godzuche.achivitapp.core.data.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.godzuche.achivitapp.core.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.core.data.local.database.relations.CollectionWithTasksEntities
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCollectionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collection: TaskCollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun reInsert(collection: TaskCollectionEntity)

    @Update
    suspend fun update(collection: TaskCollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: TaskCollectionEntity)

    @Query("SELECT * FROM ${DatabaseConstants.COLLECTION_TABLE_NAME} ORDER BY title")
    fun getAllCollection(): Flow<List<TaskCollectionEntity>>

    @Query("SELECT * FROM ${DatabaseConstants.COLLECTION_TABLE_NAME} WHERE title = :title")
    fun getCollection(title: String): Flow<TaskCollectionEntity>

    @Transaction
    @Query("SELECT * FROM ${DatabaseConstants.COLLECTION_TABLE_NAME}")
    fun getCollectionWithTasks(): Flow<List<CollectionWithTasksEntities>>

    @Transaction
    @Query("SELECT * FROM ${DatabaseConstants.COLLECTION_TABLE_NAME}  WHERE title = :collectionTitle")
    fun getCollectionWithTasksByCollectionTitle(collectionTitle: String): Flow<List<CollectionWithTasksEntities>>

    @Transaction
    @Query("SELECT * FROM ${DatabaseConstants.COLLECTION_TABLE_NAME}  WHERE category_title = :categoryTitle")
    fun getCollectionWithTasksByCategoryTitle(categoryTitle: String): Flow<List<CollectionWithTasksEntities>>

    @Transaction
    @Query("SELECT * FROM ${DatabaseConstants.COLLECTION_TABLE_NAME}  WHERE category_title = :categoryTitle")
    fun getCollectionWithTasksByCategoryTitle2(categoryTitle: String): PagingSource<Int, CollectionWithTasksEntities>

    @Query("DELETE FROM ${DatabaseConstants.COLLECTION_TABLE_NAME}")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCollections(entities: List<TaskCollectionEntity>)
}