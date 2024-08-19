package com.godzuche.achivitapp.core.data.repository

import android.icu.util.Calendar
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.core.data.local.database.dao.TaskDao
import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.core.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.asEntity
import com.godzuche.achivitapp.core.domain.model.asNewEntity
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import com.godzuche.achivitapp.core.domain.model.TaskFilter
import com.godzuche.achivitapp.core.domain.model.TaskStatus
import com.godzuche.achivitapp.feature.tasks.worker.FirebaseWorkHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class OfflineFirstTaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val firebaseWorkHelper: FirebaseWorkHelper,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TaskRepository {

    override fun getTask(id: Int): Flow<AchivitResult<Task>> = flow {
        emit(AchivitResult.Loading)
        taskDao.getTask(id)
            .catch {
                if (it is CancellationException) throw it else emit(AchivitResult.Error(it))
            }
            .collect {
                it?.let {
                    emit(AchivitResult.Success(data = it.asExternalModel()))
                } ?: emit(AchivitResult.Error(Throwable(message = "Not found.")))
            }
//        }
    }
        .flowOn(ioDispatcher)

    override suspend fun retrieveTask(id: Int): Task? {
        return withContext(ioDispatcher) {
            taskDao.getOneOffTask(id)?.asExternalModel()
        }
    }

    override fun getAllTask(
        categoryTitle: String,
        collectionTitle: String,
        status: TaskStatus,
    ): Flow<PagingData<Task>> {
        val pagingSourceFactory = {
            if (categoryTitle == DatabaseConstants.PrepopulatedData.DEFAULT_CATEGORY_TITLE) {
                if (status == TaskStatus.NONE) {
                    taskDao.getPagedTasks()
                } else {
                    taskDao.getFilteredPagedTasks(status)
                }
            } else {
                when (status) {
                    TaskStatus.NONE -> {
                        if (collectionTitle.isEmpty()) {
                            taskDao.getFilteredPagedTasks(categoryTitle)
                        } else {
                            taskDao.getFilteredPagedTasks(categoryTitle, collectionTitle)
                        }
                    }

                    else -> {
                        if (collectionTitle.isEmpty()) {
                            taskDao.getFilteredPagedTasks(categoryTitle, status)
                        } else {
                            taskDao.getFilteredPagedTasks(categoryTitle, collectionTitle, status)
                        }
                    }
                }
            }
        }

        return Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 15,
                enablePlaceholders = false,
                maxSize = 45,
                initialLoadSize = 22,
//                jumpThreshold = 50,
            ),
            pagingSourceFactory = pagingSourceFactory
        )
            .flow
            .map { pagingData ->
                pagingData.map { it.asExternalModel() }
            }
            .flowOn(ioDispatcher)

    }

    override fun searchTasksByTitle(title: String): Flow<AchivitResult<List<Task>>> = flow {
//        val dbQuery = appendDbQuery(title)
        val searchedTasks = taskDao.searchTasksByTitle(title).map { it.asExternalModel() }
        emit(AchivitResult.Success(data = searchedTasks))
    }
        .flowOn(ioDispatcher)

    override suspend fun insertTask(task: Task) {
        withContext(ioDispatcher) {
            taskDao.insert(task.asNewEntity())
        }
    }

    override suspend fun insertAndGetTaskId(task: Task): Int {
        return withContext(ioDispatcher) {
            val insertedTaskId = taskDao.insertAndGetId(task.asNewEntity()).toInt()

            Timber.tag("Add Task").d("insertedTaskId = $insertedTaskId")

            firebaseWorkHelper.addTask(task.copy(id = insertedTaskId))
            insertedTaskId
        }
    }

    override suspend fun reInsertTask(task: Task) {
        withContext(ioDispatcher) {
            taskDao.reInsert(task.asEntity())
        }
    }

    override suspend fun deleteTask(task: Task) {
        withContext(ioDispatcher) {
            taskDao.delete(task.asEntity())
            firebaseWorkHelper.deleteTask(task)
        }
    }

    override suspend fun updateTask(task: Task) {
        withContext(ioDispatcher) {
            taskDao.update(task.asEntity())
            firebaseWorkHelper.updateTask(task)
        }
    }

    override fun getTodayTasks(): Flow<List<Task>> {
        val taskDueDate = Calendar.getInstance()
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return taskDao.getTodayTasks()
            .map { taskEntities ->
                taskEntities.filter { taskEntity ->
                    taskDueDate.timeInMillis = taskEntity.dueDate
                    taskDueDate.apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                    }
                    taskDueDate == today
                }.map(TaskEntity::asExternalModel)
            }
            .flowOn(ioDispatcher)
    }

    override fun getTasksCountByStatus(status: TaskStatus): Flow<Int> {
        return taskDao.getTasksCountByStatus(status)
            .flowOn(ioDispatcher)
    }

    /*
        override fun getFilteredTasks(filter: TaskFilter): Flow<List<Task>> {
                */
    /*   return taskDao.getFilteredTasks(
                   getFilteredQuery(filter = filter)
               )*//*

    }
*/

    // Get filtered query
    /*    private fun getFilteredQuery(filter: TaskFilter): SupportSQLiteQuery {
            val query = StringBuilder()

            val categoryFilter = filter.category?.title
            val collectionFilter = filter.collection?.title

            query.append("SELECT * FROM task_table WHERE status = ?")

            when (filter.status) {
                TaskStatus.NONE -> {
                    *//*query.append(
                    "SELECT * FROM task_categories WHERE title = categoryFilter" +
                            "AND task_collections = collectionFilter" +
                            "AND status = filter.status"
                )*//*
                query.append(
                    "SELECT * FROM task_table WHERE status = ${filter.status}"
                )
            }

            TaskStatus.TODO -> {
                "SELECT * FROM task_table WHERE status = ${filter.status}"
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

            else -> {}
        }

        return SimpleSQLiteQuery(
            "SELECT * FROM task_table WHERE status = ?",
            arrayOf<Any>(filter.status.name)
        )
    }*/

}