package com.godzuche.achivitapp.core.data.repository

import androidx.paging.*
import com.godzuche.achivitapp.core.data.local.database.dao.TaskCollectionDao
import com.godzuche.achivitapp.core.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.core.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.core.data.local.database.relations.CollectionWithTasksEntities
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.TaskCollection
import com.godzuche.achivitapp.core.domain.model.asEntity
import com.godzuche.achivitapp.core.domain.repository.TaskCollectionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class TaskCollectionRepositoryImpl @Inject constructor(
    private val collectionDao: TaskCollectionDao
) : TaskCollectionRepository {
    override fun getCollection(title: String): Flow<TaskCollection> =
        collectionDao.getCollection(title = title).map(TaskCollectionEntity::asExternalModel)

    override fun getAllCollection(): Flow<List<TaskCollection>> {
        return collectionDao.getAllCollection()
            .map { it.map(TaskCollectionEntity::asExternalModel) }
    }

    override suspend fun insertCollection(collection: TaskCollection) {
        collectionDao.insert(collection = collection.asEntity())
    }

    override suspend fun updateCollection(collection: TaskCollection) {
        collectionDao.update(collection = collection.asEntity())
    }

    override fun getCollectionsWithTasksByCategoryTitle(categoryTitle: String): Flow<List<CollectionWithTasksEntities>> {
        return collectionDao.getCollectionWithTasksByCategoryTitle(categoryTitle = categoryTitle)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCollectionsWithTasksByCategoryTitle2(categoryTitle: String): Flow<PagingData<Task>> {
        /*return collectionDao.getCollectionWithTasksByCategoryTitle2(categoryTitle = categoryTitle)
            .map { it.map { it.asExternalModel() } }*/
        val pagingSourceFactory = {
            collectionDao.getCollectionWithTasksByCategoryTitle2(categoryTitle = categoryTitle)
        }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 100
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .mapLatest { pagingData ->
                pagingData.flatMap {
                    it.tasks.map {
                        it.asExternalModel()
                    }
                }
            }
            .distinctUntilChanged()
    }
}