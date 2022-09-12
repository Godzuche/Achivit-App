package com.godzuche.achivitapp.feature_home.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_home.data.local.TaskCategoryDao
import com.godzuche.achivitapp.feature_home.data.local.TaskCollectionDao
import com.godzuche.achivitapp.feature_home.data.local.TaskDao
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_home.data.local.relations.CategoryWithCollections
import com.godzuche.achivitapp.feature_home.domain.model.Task
import com.godzuche.achivitapp.feature_home.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_home.presentation.util.TaskFilter
import com.godzuche.achivitapp.feature_home.presentation.util.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val categoryDao: TaskCategoryDao,
    private val collectionDao: TaskCollectionDao,
) : TaskRepository {

    override fun getTask(id: Int): Flow<Resource<Task>> = flow {
        taskDao.getTask(id).collect {
            emit(Resource.Success(data = it.toTask()))
        }
    }

    override fun getTaskOnce(id: Int): Task {
        Timber.d("Reminder", "getTaskOnce() called in repo")
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
        Timber.tag("Reminder").d("updateTask called in repo")
        taskDao.update(task.toTaskEntity())
    }

/*    override suspend fun updateTaskStatus(taskId: Int, status: TaskStatus) {
        getTask(taskId).distinctUntilChanged().collect() {
            Timber.tag("Reminder").d("updateTaskStatus called in repo")
            it.data?.let { task -> updateTask(task.copy(status = status)) }
        }
    }*/

    override fun getCategory(title: String): Flow<TaskCategoryEntity> {
        return categoryDao.getCategory(title)
    }

    override fun getLastInsertedTask(): Flow<Task> {
        return taskDao.getLastInsertedTask().map { it.first().toTask() }
    }

    override fun getAllCategory(): Flow<List<TaskCategoryEntity>> {
        return categoryDao.getAllCategory()
    }

    override suspend fun insertCategory(category: TaskCategoryEntity) {
        categoryDao.insert(category)
    }

    override suspend fun updateCategory(category: TaskCategoryEntity) {
        categoryDao.update(category)
    }

    override fun getCategoryWithCollectionByTitle(categoryTitle: String): Flow<List<CategoryWithCollections>> {
        return categoryDao.getCategoryWithCollectionByTitle(categoryTitle)
    }

    override fun getCollection(title: String): Flow<TaskCollectionEntity> {
        return collectionDao.getCollection(title)
    }

    override fun getAllCollection(): Flow<List<TaskCollectionEntity>> {
        return collectionDao.getAllCollection()
    }

    override suspend fun insertCollection(collection: TaskCollectionEntity) {
        return collectionDao.insert(collection)
    }

    override suspend fun updateCollection(collection: TaskCollectionEntity) {
        return collectionDao.update(collection)
    }

    private fun getFilteredQuery(filter: TaskFilter): SupportSQLiteQuery {
        val query = StringBuilder()

        val categoryFilter = filter.category?.title
        val collectionFilter = filter.collection?.title
        when (filter.status) {
            TaskStatus.NONE -> {
                query.append(
                    "SELECT * FROM task_categories WHERE title = categoryFilter" +
                            "AND task_collections = collectionFilter" +
                            "AND status = filter.status"
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
            TaskStatus.COMPLETED_TASKS -> {
                //
            }
        }

        return SimpleSQLiteQuery(query.toString())
    }

}