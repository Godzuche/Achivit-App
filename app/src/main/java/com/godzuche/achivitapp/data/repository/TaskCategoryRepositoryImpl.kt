package com.godzuche.achivitapp.data.repository

import com.godzuche.achivitapp.data.local.database.dao.TaskCategoryDao
import com.godzuche.achivitapp.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollectionsAndTasksEntities
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollectionsEntities
import com.godzuche.achivitapp.data.local.database.relations.asExternalModel
import com.godzuche.achivitapp.domain.model.CategoryWithCollections
import com.godzuche.achivitapp.domain.model.CategoryWithCollectionsAndTasks
import com.godzuche.achivitapp.domain.model.TaskCategory
import com.godzuche.achivitapp.domain.model.asEntity
import com.godzuche.achivitapp.domain.repository.TaskCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskCategoryRepositoryImpl @Inject constructor(
    private val categoryDao: TaskCategoryDao
) : TaskCategoryRepository {

    override suspend fun retrieveAllCategories(): List<TaskCategory> =
        categoryDao.retrieveAllCategories().map(TaskCategoryEntity::asExternalModel)

    override fun getCategory(title: String): Flow<TaskCategory> =
        categoryDao.getCategory(title = title).map(TaskCategoryEntity::asExternalModel)

    override fun getAllCategories(): Flow<List<TaskCategory>> {
        return categoryDao.getAllCategories().map { it.map(TaskCategoryEntity::asExternalModel) }
    }

    override suspend fun insertCategory(category: TaskCategory) {
        categoryDao.insert(category = category.asEntity())
    }

    override suspend fun updateCategory(category: TaskCategory) {
        categoryDao.update(category = category.asEntity())
    }

    override fun getCategoryWithCollections(): Flow<List<CategoryWithCollections>> {
        return categoryDao.getCategoriesWithCollections()
            .map { it.map(CategoryWithCollectionsEntities::asExternalModel) }
    }

    override fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>> {
        return categoryDao
            .getCategoryWithCollectionsByTitle(categoryTitle = categoryTitle)
            .map { it.map(CategoryWithCollectionsEntities::asExternalModel) }
    }

    override fun getCategoryWithCollectionsAndTasks(): Flow<List<CategoryWithCollectionsAndTasks>> =
        categoryDao.getCategoryWithCollectionsAndTasks()
            .map { it.map(CategoryWithCollectionsAndTasksEntities::asExternalModel) }
}