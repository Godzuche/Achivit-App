package com.godzuche.achivitapp.core.domain.use_case

import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTasksByTitle(
    private val repository: TaskRepository,
) {
    operator fun invoke(title: String): Flow<Resource<List<Task>>> {
        if (title.isBlank()) {
            return flow { }
        }
        return repository.searchTasksByTitle(title)
    }
}