package com.godzuche.achivitapp.feature_task.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.local.TaskDao
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {

    override fun getTask(id: Long): Flow<Resource<Task>> = flow {
        dao.getTask(id).collect {
            emit(Resource.Success(data = it.toTask()))
        }
    }

/*    override fun getAllTask(): Flow<Resource<List<Task>>> = flow {
        dao.getAllTasks().collect { tasks ->
            val allTasks = tasks.map { it.toTask() }
            emit(Resource.Success(data = allTasks))
        }
    }*/
    /*override fun getAllTask(): Flow<Resource<List<Task>>> = flow {
        dao.getAllTasks().collect { tasks ->
            val allTasks = tasks.map { it.toTask() }
            emit(Resource.Success(data = allTasks))
        }
    }*/

    override fun getAllTask(): Flow<PagingData<Task>> {
        val pagingSourceFactory = {
            dao.getAllTasks()
        }

        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                maxSize = 100
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .map { pagingData ->
                pagingData.map {
                    it.toTask()
                }
            }

    }

    override fun searchTasksByTitle(title: String): Flow<Resource<List<Task>>> = flow {
//        val dbQuery = appendDbQuery(title)
        val searchedTasks = dao.searchTasksByTitle(title).map { it.toTask() }
        emit(Resource.Success(data = searchedTasks))
    }

    override suspend fun insertTask(task: Task) {
        dao.insert(task.toNewTaskEntity())
    }

    override suspend fun reInsertTask(task: Task) {
        dao.reInsert(task.toTaskEntity())
    }

    override suspend fun deleteTask(task: Task) {
        dao.delete(task.toTaskEntity())
    }

    override suspend fun updateTask(task: Task) {
        dao.update(task.toTaskEntity())
    }
}