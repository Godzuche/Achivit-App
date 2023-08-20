package com.godzuche.achivitapp.domain.repository

import com.godzuche.achivitapp.domain.model.CategoryWithCollections
import com.godzuche.achivitapp.domain.model.CategoryWithCollectionsAndTasks
import com.godzuche.achivitapp.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow

interface TaskCategoryRepository {
    suspend fun retrieveAllCategories(): List<TaskCategory>
    fun getCategory(title: String): Flow<TaskCategory>
    fun getAllCategories(): Flow<List<TaskCategory>>
    suspend fun insertCategory(category: TaskCategory)
    suspend fun updateCategory(category: TaskCategory)
    fun getCategoryWithCollections(): Flow<List<CategoryWithCollections>>
    fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>>
    fun getCategoryWithCollectionsAndTasks(): Flow<List<CategoryWithCollectionsAndTasks>>
}