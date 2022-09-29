package com.godzuche.achivitapp.feature_task.domain.repository

import androidx.paging.PagingData
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_task.data.local.relations.CategoryWithCollections
import com.godzuche.achivitapp.feature_task.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTask(id: Int): Flow<Resource<Task>>

    fun getTaskOnce(id: Int): Task

    //    fun getAllTask(): Flow<Resource<List<Task>>>
    fun getAllTask(): Flow<PagingData<Task>>

    fun searchTasksByTitle(title: String): Flow<Resource<List<Task>>>

    suspend fun insertTask(task: Task)

    suspend fun insertAndGetTask(task: Task): Int

    fun getLastInsertedTask(): Flow<Task>

    suspend fun reInsertTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun updateTask(task: Task)

    fun getCategory(title: String): Flow<TaskCategoryEntity>
    fun getAllCategory(): Flow<List<TaskCategoryEntity>>
    suspend fun insertCategory(category: TaskCategoryEntity)
    suspend fun updateCategory(category: TaskCategoryEntity)
    fun getCategoryWithCollectionByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>>

    fun getCollection(title: String): Flow<TaskCollectionEntity>
    fun getAllCollection(): Flow<List<TaskCollectionEntity>>
    suspend fun insertCollection(collection: TaskCollectionEntity)
    suspend fun updateCollection(collection: TaskCollectionEntity)

}