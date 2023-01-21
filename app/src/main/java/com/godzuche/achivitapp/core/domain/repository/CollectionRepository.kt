package com.godzuche.achivitapp.core.domain.repository

import com.godzuche.achivitapp.core.data.local.entity.TaskCollection
import com.godzuche.achivitapp.core.data.local.relations.CollectionWithTasks
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getCollection(title: String): Flow<TaskCollection>
    fun getAllCollection(): Flow<List<TaskCollection>>
    suspend fun insertCollection(collection: TaskCollection)
    suspend fun updateCollection(collection: TaskCollection)
    fun getCollectionsWithTasksByCategoryTitle(categoryTitle: String): Flow<List<CollectionWithTasks>>
}