package com.godzuche.achivitapp.core.domain.usecase

import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import javax.inject.Inject

class RetrieveTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(id: Int): Task? {
        return taskRepository.retrieveTask(id)
    }
}