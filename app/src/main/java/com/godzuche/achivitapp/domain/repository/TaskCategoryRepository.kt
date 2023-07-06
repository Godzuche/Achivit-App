package com.godzuche.achivitapp.domain.repository

import com.godzuche.achivitapp.data.local.database.model.TaskCategory
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollections
import com.godzuche.achivitapp.data.local.database.relations.CategoryWithCollectionsAndTasks
import kotlinx.coroutines.flow.Flow

interface TaskCategoryRepository {
    fun getCategory(title: String): Flow<TaskCategory>
    fun getAllCategory(): Flow<List<TaskCategory>>
    suspend fun insertCategory(category: TaskCategory)
    suspend fun updateCategory(category: TaskCategory)
    fun getCategoryWithCollections(): Flow<List<CategoryWithCollections>>
    fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>>
    fun getCategoryWithCollectionsAndTasks(): Flow<List<CategoryWithCollectionsAndTasks>>
}