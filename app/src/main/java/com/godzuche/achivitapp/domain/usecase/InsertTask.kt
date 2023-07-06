package com.godzuche.achivitapp.domain.usecase

import com.godzuche.achivitapp.data.repository.TaskRepositoryImpl
import com.godzuche.achivitapp.domain.model.Task

class InsertTask(
    private val repositoryImpl: TaskRepositoryImpl,
) {
    suspend operator fun invoke(task: Task) {
        repositoryImpl.insertTask(task)
    }
}