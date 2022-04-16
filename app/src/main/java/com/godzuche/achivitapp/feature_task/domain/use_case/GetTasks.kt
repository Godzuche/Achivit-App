package com.godzuche.achivitapp.feature_task.domain.use_case

import androidx.paging.PagingData
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasks(
    private val repository: TaskRepository,
) {
    /*operator fun invoke(): Flow<Resource<List<Task>>> {
        return repository.getAllTask()
    }*/
    operator fun invoke(): Flow<PagingData<Task>> {
        return repository.getAllTask()
    }
}