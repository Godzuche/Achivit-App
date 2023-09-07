package com.godzuche.achivitapp.domain.repository

import androidx.paging.PagingData
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.feature.tasks.util.TaskFilter
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTask(id: Int): Flow<AchivitResult<Task>>

    fun retrieveTask(id: Int): Task?

    //    fun getAllTask(): Flow<Resource<List<Task>>>
    fun getAllTask(
        categoryTitle: String,
        collectionTitle: String,
        status: TaskStatus
    ): Flow<PagingData<Task>>

    fun searchTasksByTitle(title: String): Flow<AchivitResult<List<Task>>>

    suspend fun insertTask(task: Task)

    suspend fun insertAndGetTaskId(task: Task): Int

//    fun getLastInsertedTask(): Flow<Task>

    suspend fun reInsertTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun updateTask(task: Task)

    fun getTodayTasks(): Flow<List<Task>>

    fun getFilteredTasks(filter: TaskFilter): Flow<List<Task>>

}