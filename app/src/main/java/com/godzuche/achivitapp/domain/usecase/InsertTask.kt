package com.godzuche.achivitapp.domain.usecase

import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.repository.TaskRepository
import javax.inject.Inject

class InsertTask @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.insertTask(task)
    }
}