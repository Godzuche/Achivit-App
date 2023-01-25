package com.godzuche.achivitapp.core.domain.use_case

import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTask @Inject constructor(
    private val repository: TaskRepository,
) {
    operator fun invoke(id: Int): Flow<Resource<Task>> {
        return repository.getTask(id)
    }
}