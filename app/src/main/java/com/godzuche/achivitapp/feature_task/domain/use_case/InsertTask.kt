package com.godzuche.achivitapp.feature_task.domain.use_case

import com.godzuche.achivitapp.feature_task.data.repository.TaskRepositoryImpl
import com.godzuche.achivitapp.feature_task.domain.model.Task

class InsertTask(
    private val repositoryImpl: TaskRepositoryImpl,
) {
    suspend operator fun invoke(task: Task) {
        repositoryImpl.insertTask(task)
    }
}