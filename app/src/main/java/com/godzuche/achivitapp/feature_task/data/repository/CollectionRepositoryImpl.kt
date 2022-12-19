package com.godzuche.achivitapp.feature_task.data.repository

import com.godzuche.achivitapp.feature_task.data.local.TaskCollectionDao
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection
import com.godzuche.achivitapp.feature_task.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectionRepositoryImpl(private val collectionDao: TaskCollectionDao) :
    CollectionRepository {
    override fun getCollection(title: String): Flow<TaskCollection> =
        collectionDao.getCollection(title = title).map { it.toTaskCollection() }

    override fun getAllCollection(): Flow<List<TaskCollection>> {
        return collectionDao.getAllCollection().map { collectionEntities ->
            collectionEntities.map { it.toTaskCollection() }
        }
    }

    override suspend fun insertCollection(collection: TaskCollection) {
        collectionDao.insert(collection = collection.toTaskCollectionEntity())
    }

    override suspend fun updateCollection(collection: TaskCollection) {
        collectionDao.update(collection = collection.toTaskCollectionEntity())
    }
}