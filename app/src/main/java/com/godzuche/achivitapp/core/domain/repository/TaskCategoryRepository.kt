package com.godzuche.achivitapp.core.domain.repository

import com.godzuche.achivitapp.core.domain.model.CategoryWithCollections
import com.godzuche.achivitapp.core.domain.model.CategoryWithCollectionsAndTasks
import com.godzuche.achivitapp.core.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow

interface TaskCategoryRepository {
    suspend fun retrieveAllCategories(): List<TaskCategory>
    fun getCategory(title: String): Flow<TaskCategory>
    fun getAllCategories(): Flow<List<TaskCategory>>
    suspend fun insertCategory(category: TaskCategory)
    suspend fun updateCategory(category: TaskCategory)
    fun getCategoryWithCollections(): Flow<List<com.godzuche.achivitapp.core.domain.model.CategoryWithCollections>>
    fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<com.godzuche.achivitapp.core.domain.model.CategoryWithCollections>>
    fun getCategoryWithCollectionsAndTasks(): Flow<List<com.godzuche.achivitapp.core.domain.model.CategoryWithCollectionsAndTasks>>
}