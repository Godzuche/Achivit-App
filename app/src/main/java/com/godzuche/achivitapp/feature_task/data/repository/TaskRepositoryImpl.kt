package com.godzuche.achivitapp.feature_task.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.local.TaskDao
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.presentation.util.TaskFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {

    override fun getTask(id: Int): Flow<Resource<Task>> = flow {
        taskDao.getTask(id).collect {
            emit(Resource.Success(data = it.toTask()))
        }
    }

    override fun getTaskOnce(id: Int): Task {
        return taskDao.getTaskOnce(id).toTask()
    }

    override fun getAllTask(): Flow<PagingData<Task>> {
        val pagingSourceFactory = {
            taskDao.getAllTasks()
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
            }.distinctUntilChanged()

    }

    override fun searchTasksByTitle(title: String): Flow<Resource<List<Task>>> = flow {
//        val dbQuery = appendDbQuery(title)
        val searchedTasks = taskDao.searchTasksByTitle(title).map { it.toTask() }
        emit(Resource.Success(data = searchedTasks))
    }

    override suspend fun insertTask(task: Task) {
        taskDao.insert(task.toNewTaskEntity())
    }

    override suspend fun insertAndGetTask(task: Task): Int =
        taskDao.insertAndGetId(task.toNewTaskEntity()).toInt()

    override suspend fun reInsertTask(task: Task) {
        taskDao.reInsert(task.toTaskEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toTaskEntity())
    }

    override suspend fun updateTask(task: Task) {
        Timber.tag("CheckBox")
            .i("updateTask called in repo with completed status: ${task.isCompleted}")
        taskDao.update(task.toTaskEntity())
    }

/*    override fun getLastInsertedTask(): Flow<Task> {
        return taskDao.getLastInsertedTask().map { it.first().toTask() }
    }*/

    override fun getFilteredTasks(filter: TaskFilter): Flow<List<Task>> {
        return taskDao.getFilteredTasks(
            getFilteredQuery(filter = filter)
        )
            .map { taskEntities ->
                taskEntities.map { it.toTask() }
            }
    }

    // Get filtered query
    private fun getFilteredQuery(filter: TaskFilter): SupportSQLiteQuery {
        val query = StringBuilder()

        val categoryFilter = filter.category?.title
        val collectionFilter = filter.collection?.title

        query.append("SELECT * FROM task_table WHERE status = ?")

        /*when (filter.status) {
            TaskStatus.NONE -> {
                *//*query.append(
                    "SELECT * FROM task_categories WHERE title = categoryFilter" +
                            "AND task_collections = collectionFilter" +
                            "AND status = filter.status"
                )*//*
                query.append(
                    "SELECT * FROM task_table WHERE status = filter.status"
                )
            }
            TaskStatus.TODO -> {
                //
            }
            TaskStatus.IN_PROGRESS -> {
                //
            }
            TaskStatus.RUNNING_LATE -> {
                //
            }
            TaskStatus.COMPLETED -> {
                //
            }
        }*/

        return SimpleSQLiteQuery(
            "SELECT * FROM task_table WHERE status = ?",
            arrayOf<Any>(filter.status.name)
        )
    }

}