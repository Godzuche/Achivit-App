package com.godzuche.achivitapp.core.domain.use_case

import androidx.paging.PagingData
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
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