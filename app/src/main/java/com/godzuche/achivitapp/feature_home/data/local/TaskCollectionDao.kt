package com.godzuche.achivitapp.feature_home.data.local

import androidx.room.*
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_home.data.local.relations.CollectionWithTasks
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

    @Query("SELECT * FROM task_collections ORDER BY title")
    fun getAllCollection(): Flow<List<TaskCollectionEntity>>

    @Query("SELECT * FROM task_collections WHERE title = :title")
    fun getCollection(title: String): Flow<TaskCollectionEntity>

    @Transaction
    @Query("SELECT * FROM task_collections")
    fun getCollectionWithTasks(): Flow<List<CollectionWithTasks>>

    @Transaction
    @Query("SELECT * FROM task_collections  WHERE title = :collectionTitle")
    fun getCollectionWithTasksByCollectionTitle(collectionTitle: String): Flow<List<CollectionWithTasks>>

    @Query("DELETE FROM task_collections")
    suspend fun deleteAll()
}