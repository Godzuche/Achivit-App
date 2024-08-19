package com.godzuche.achivitapp.core.data.repository

import com.godzuche.achivitapp.core.data.local.database.dao.TaskDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskFtsDao
import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.core.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.core.data.repository.util.appendFtsDbQuery
import com.godzuche.achivitapp.core.domain.model.SearchResult
import com.godzuche.achivitapp.core.domain.repository.SearchContentsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class DefaultSearchContentsRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val taskFtsDao: TaskFtsDao
) : SearchContentsRepository {
    /*suspend fun populateFtsDao() {
        withContext(ioDispatcher){
            taskFtsDao.
        }
    }*/
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        val taskIds = taskFtsDao.searchAllTasks(appendFtsDbQuery(searchQuery))

//        Only fetch the necessary TaskEntity objects based on the retrieved IDs,
//        potentially reducing database load.
        val tasksFlow = taskIds.mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest(taskDao::getTaskEntitiesByIds)

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