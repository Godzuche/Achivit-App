package com.godzuche.achivitapp.domain.repository

import com.godzuche.achivitapp.data.local.entity.TaskCategory
import com.godzuche.achivitapp.data.local.relations.CategoryWithCollections
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategory(title: String): Flow<TaskCategory>
    fun getAllCategory(): Flow<List<TaskCategory>>
    suspend fun insertCategory(category: TaskCategory)
    suspend fun updateCategory(category: TaskCategory)
    fun getCategoriesWithCollections(): Flow<List<CategoryWithCollections>>
    fun getCategoryWithCollectionsByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>>
}