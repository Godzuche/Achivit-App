package com.godzuche.achivitapp.feature_task.domain.repository

import androidx.paging.PagingData
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.local.entity.Category
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection
import com.godzuche.achivitapp.feature_task.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTask(id: Long): Flow<Resource<Task>>

    //    fun getAllTask(): Flow<Resource<List<Task>>>
    fun getAllTask(): Flow<PagingData<Task>>

    fun searchTasksByTitle(title: String): Flow<Resource<List<Task>>>

    suspend fun insertTask(task: Task)

    fun getLastInsertedTask(): Flow<Task>

    suspend fun reInsertTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun updateTask(task: Task)

    //
    fun getCategory(id: Long): Flow<Category>
    fun getCategoryEntity(id: Long): Flow<TaskCategoryEntity>
    fun getAllCategory(): Flow<List<Category>>
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)

    //
    fun getCollection(id: Long): Flow<TaskCollection>
    fun getAllCollection(): Flow<List<TaskCollection>>
    suspend fun insertCollection(collection: TaskCollection)
    suspend fun updateCollection(collection: TaskCollection)

}