package com.godzuche.achivitapp.feature_task.domain.repository

import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getCollection(title: String): Flow<TaskCollection>
    fun getAllCollection(): Flow<List<TaskCollection>>
    suspend fun insertCollection(collection: TaskCollection)
    suspend fun updateCollection(collection: TaskCollection)
}