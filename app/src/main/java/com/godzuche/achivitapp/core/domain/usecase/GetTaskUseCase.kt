package com.godzuche.achivitapp.core.domain.usecase

import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    operator fun invoke(id: Int): Flow<AchivitResult<Task>> {
        return repository.getTask(id)
    }
}