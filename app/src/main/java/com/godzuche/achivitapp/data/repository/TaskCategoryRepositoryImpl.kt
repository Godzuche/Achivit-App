package com.godzuche.achivitapp.data.repository

import com.godzuche.achivitapp.data.local.database.dao.TaskCategoryDao
import com.godzuche.achivitapp.data.local.database.model.TaskCategory
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollections
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollectionsAndTasks
import com.godzuche.achivitapp.domain.repository.TaskCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class TaskCategoryRepositoryImpl @Inject constructor(
    private val categoryDao: TaskCategoryDao
) : TaskCategoryRepository {
    override fun getCategory(title: String): Flow<TaskCategory> =
        categoryDao.getCategory(title = title).map { it.toTaskCategory() }

    override fun getAllCategory(): Flow<List<TaskCategory>> {
        return categoryDao.getAllCategory().map { categoriesEntities ->
            categoriesEntities.map { it.toTaskCategory() }
        }
    }

    override suspend fun insertCategory(category: TaskCategory) {
        categoryDao.insert(category = category.toCategoryEntity())
    }

    override suspend fun updateCategory(category: TaskCategory) {
        categoryDao.update(category = category.toCategoryEntity())
    }

    override fun getCategoryWithCollections(): Flow<List<CategoryWithCollections>> {
        return categoryDao.getCategoriesWithCollections()
    }

    override fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>> {
        return categoryDao.getCategoryWithCollectionsByTitle(categoryTitle = categoryTitle)
    }

    override fun getCategoryWithCollectionsAndTasks(): Flow<List<CategoryWithCollectionsAndTasks>> =
        categoryDao.getCategoryWithCollectionsAndTasks()
}