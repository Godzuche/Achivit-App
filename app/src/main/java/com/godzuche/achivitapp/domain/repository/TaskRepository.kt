package com.godzuche.achivitapp.domain.repository

import androidx.paging.PagingData
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.feature_home.presentation.core.util.Resource
import com.godzuche.achivitapp.feature_tasks_feed.util.TaskFilter
import com.godzuche.achivitapp.feature_tasks_feed.util.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTask(id: Int): Flow<Resource<Task>>

    fun getTaskOnce(id: Int): Task

    //    fun getAllTask(): Flow<Resource<List<Task>>>
    fun getAllTask(categoryTitle: String, status: TaskStatus): Flow<PagingData<Task>>

    fun searchTasksByTitle(title: String): Flow<Resource<List<Task>>>

    suspend fun insertTask(task: Task)

    suspend fun insertAndGetTask(task: Task): Int

//    fun getLastInsertedTask(): Flow<Task>

    suspend fun reInsertTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun updateTask(task: Task)

    fun getTodayTasks(): Flow<List<Task>>

    fun getFilteredTasks(filter: TaskFilter): Flow<List<Task>>

}