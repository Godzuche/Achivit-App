package com.godzuche.achivitapp.feature_task.data.repository

import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.local.TaskDao
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {

    override fun getTask(id: Int): Flow<Resource<Task>> = flow {
        dao.getTask(id).collect {
            emit(Resource.Success(data = it.toTask()))
        }
    }

    override fun getAllTask(): Flow<Resource<List<Task>>> = flow {
        dao.getAllTasks().collect { tasks ->
            val allTasks = tasks.map { it.toTask() }
            emit(Resource.Success(data = allTasks))
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