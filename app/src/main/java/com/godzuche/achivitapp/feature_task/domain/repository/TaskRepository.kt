package com.godzuche.achivitapp.feature_task.domain.repository

import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTask(id: Int): Flow<Resource<Task>>

    fun getAllTask(): Flow<Resource<List<Task>>>

    fun searchTasksByTitle(title: String): Flow<Resource<List<Task>>>

    suspend fun insertTask(task: Task)

    suspend fun reInsertTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun updateTask(task: Task)
}