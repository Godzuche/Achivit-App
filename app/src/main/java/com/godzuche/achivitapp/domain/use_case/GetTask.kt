package com.godzuche.achivitapp.domain.use_case

import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_home.presentation.core.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTask @Inject constructor(
    private val repository: TaskRepository,
) {
    operator fun invoke(id: Int): Flow<Resource<Task>> {
        return repository.getTask(id)
    }
}