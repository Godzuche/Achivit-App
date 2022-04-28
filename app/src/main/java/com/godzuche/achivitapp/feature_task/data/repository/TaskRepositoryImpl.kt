package com.godzuche.achivitapp.feature_task.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.local.TaskCategoryDao
import com.godzuche.achivitapp.feature_task.data.local.TaskCollectionDao
import com.godzuche.achivitapp.feature_task.data.local.TaskDao
import com.godzuche.achivitapp.feature_task.data.local.entity.Category
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val categoryDao: TaskCategoryDao,
    private val collectionDao: TaskCollectionDao,
) : TaskRepository {

    override fun getTask(id: Long): Flow<Resource<Task>> = flow {
        taskDao.getTask(id).collect {
            emit(Resource.Success(data = it.toTask()))
        }
    }

/*    override fun getAllTask(): Flow<Resource<List<Task>>> = flow {
        taskDao.getAllTasks().collect { tasks ->
            val allTasks = tasks.map { it.toTask() }
            emit(Resource.Success(data = allTasks))
        }
    }*/
    /*override fun getAllTask(): Flow<Resource<List<Task>>> = flow {
        taskDao.getAllTasks().collect { tasks ->
            val allTasks = tasks.map { it.toTask() }
            emit(Resource.Success(data = allTasks))
        }
    }*/

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
            }

    }

    override fun searchTasksByTitle(title: String): Flow<Resource<List<Task>>> = flow {
//        val dbQuery = appendDbQuery(title)
        val searchedTasks = taskDao.searchTasksByTitle(title).map { it.toTask() }
        emit(Resource.Success(data = searchedTasks))
    }

    override suspend fun insertTask(task: Task) {
        taskDao.insert(task.toNewTaskEntity())
    }

    override suspend fun reInsertTask(task: Task) {
        taskDao.reInsert(task.toTaskEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toTaskEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toTaskEntity())
    }

    override fun getCategory(id: Long): Flow<Category> {
        return categoryDao.getCategory(id).map { it.toCategory() }
    }

    override fun getCategoryEntity(id: Long): Flow<TaskCategoryEntity> {
        return categoryDao.getCategory(id)
    }

    override fun getLastInsertedTask(): Flow<Task> {
        return taskDao.getLastInsertedTask().map { it.first().toTask() }
    }

    override fun getAllCategory(): Flow<List<Category>> {
        return categoryDao.getAllCategory().map { categories ->
            categories.map { it.toCategory() }
        }
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insert(category.toNewTaskCategoryEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.update(category.toTaskCategoryEntity())
    }

    override fun getCollection(id: Long): Flow<TaskCollection> {
        return collectionDao.getCollection(id).map { it.toCollection() }
    }

    override fun getAllCollection(): Flow<List<TaskCollection>> {
        return collectionDao.getAllCollection().map { collections ->
            collections.map { it.toCollection() }
        }
    }

    override suspend fun insertCollection(collection: TaskCollection) {
        return collectionDao.insert(collection.toNewTaskCollectionEntity())
    }

    override suspend fun updateCollection(collection: TaskCollection) {
        return collectionDao.update(collection.toTaskCollectionEntity())
    }
}