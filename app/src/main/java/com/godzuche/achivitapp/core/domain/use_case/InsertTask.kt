package com.godzuche.achivitapp.core.domain.use_case

import com.godzuche.achivitapp.core.data.repository.TaskRepositoryImpl
import com.godzuche.achivitapp.core.domain.model.Task

class InsertTask(
    private val repositoryImpl: TaskRepositoryImpl,
) {
    suspend operator fun invoke(task: Task) {
        repositoryImpl.insertTask(task)
    }
}