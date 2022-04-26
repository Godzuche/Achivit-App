package com.godzuche.achivitapp.feature_task.data.local

import androidx.room.*
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCollectionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(collection: TaskCollectionEntity)

    @Update
    suspend fun update(collection: TaskCollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: TaskCollectionEntity)

    @Query("SELECT * FROM task_collections ORDER BY collectionId")
    fun getAllCollection(): Flow<List<TaskCollectionEntity>>

    @Query("SELECT * FROM task_collections WHERE collectionId = :id")
    fun getCollection(id: Long): Flow<TaskCollectionEntity>

    @Query("DELETE FROM task_collections")
    suspend fun deleteAll()
}