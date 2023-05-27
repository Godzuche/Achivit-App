package com.godzuche.achivitapp.domain.use_case

import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTasksByTitle(
    private val repository: TaskRepository,
) {
    operator fun invoke(title: String): Flow<AchivitResult<List<Task>>> {
        if (title.isBlank()) {
            return flow { }
        }
        return repository.searchTasksByTitle(title)
    }
}