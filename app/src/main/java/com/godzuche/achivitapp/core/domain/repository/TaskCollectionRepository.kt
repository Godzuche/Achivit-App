package com.godzuche.achivitapp.core.domain.repository

import androidx.paging.PagingData
import com.godzuche.achivitapp.core.data.local.database.relations.CollectionWithTasksEntities
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.TaskCollection
import kotlinx.coroutines.flow.Flow

interface TaskCollectionRepository {
    fun getCollection(title: String): Flow<TaskCollection>
    fun getAllCollection(): Flow<List<TaskCollection>>
    suspend fun insertCollection(collection: TaskCollection)
    suspend fun updateCollection(collection: TaskCollection)
    fun getCollectionsWithTasksByCategoryTitle(categoryTitle: String): Flow<List<CollectionWithTasksEntities>>
    fun getCollectionsWithTasksByCategoryTitle2(categoryTitle: String): Flow<PagingData<Task>>
}