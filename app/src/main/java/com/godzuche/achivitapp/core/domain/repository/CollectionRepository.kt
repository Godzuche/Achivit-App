package com.godzuche.achivitapp.core.domain.repository

import androidx.paging.PagingData
import com.godzuche.achivitapp.core.data.local.entity.TaskCollection
import com.godzuche.achivitapp.core.data.local.relations.CollectionWithTasks
import com.godzuche.achivitapp.core.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getCollection(title: String): Flow<TaskCollection>
    fun getAllCollection(): Flow<List<TaskCollection>>
    suspend fun insertCollection(collection: TaskCollection)
    suspend fun updateCollection(collection: TaskCollection)
    fun getCollectionsWithTasksByCategoryTitle(categoryTitle: String): Flow<List<CollectionWithTasks>>
    fun getCollectionsWithTasksByCategoryTitle2(categoryTitle: String): Flow<PagingData<Task>>
}