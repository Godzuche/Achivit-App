package com.godzuche.achivitapp.feature_task.data.repository

import com.godzuche.achivitapp.feature_task.data.local.TaskCategoryDao
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategory
import com.godzuche.achivitapp.feature_task.data.local.relations.CategoryWithCollections
import com.godzuche.achivitapp.feature_task.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(private val categoryDao: TaskCategoryDao) : CategoryRepository {
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

    override fun getCategoriesWithCollections(): Flow<List<CategoryWithCollections>> {
        return categoryDao.getCategoriesWithCollections()
    }

    override fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>> {
        return categoryDao.getCategoryWithCollectionsByTitle(categoryTitle = categoryTitle)
    }
}