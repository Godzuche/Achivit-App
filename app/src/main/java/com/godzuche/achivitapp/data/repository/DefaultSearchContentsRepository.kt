package com.godzuche.achivitapp.data.repository

import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.data.local.database.dao.TaskDao
import com.godzuche.achivitapp.data.local.database.dao.TaskFtsDao
import com.godzuche.achivitapp.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.domain.model.SearchResult
import com.godzuche.achivitapp.domain.repository.SearchContentsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class DefaultSearchContentsRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val taskFtsDao: TaskFtsDao,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : SearchContentsRepository {
    /*suspend fun populateFtsDao() {
        withContext(ioDispatcher){
            taskFtsDao.
        }
    }*/
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        val taskIds = taskFtsDao.searchAllTasks("*$searchQuery*")

        val tasksFlow = taskIds.mapLatest { it.toSet() }
            .distinctUntilChanged().flatMapLatest(taskDao::getTaskEntitiesByIds)

        return tasksFlow.mapLatest {
            it.map(TaskEntity::asExternalModel)
        }
            .distinctUntilChanged()
            .mapLatest { tasks ->
                SearchResult(tasks = tasks)
            }
    }

    override fun getSearchContentsCount(): Flow<Int> =
        taskFtsDao.getCount()
}