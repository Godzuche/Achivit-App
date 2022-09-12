package com.godzuche.achivitapp.feature_home.domain.use_case

import com.godzuche.achivitapp.feature_home.data.repository.TaskRepositoryImpl
import com.godzuche.achivitapp.feature_home.domain.model.Task

class InsertTask(
    private val repositoryImpl: TaskRepositoryImpl,
) {
    suspend operator fun invoke(task: Task) {
        repositoryImpl.insertTask(task)
    }
}